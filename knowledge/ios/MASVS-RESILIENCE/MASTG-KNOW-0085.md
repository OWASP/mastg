---
masvs_category: MASVS-RESILIENCE
platform: ios
title: Anti-Debugging Detection
best-practices: [MASTG-BEST-0074]
---

Debugging is a powerful runtime analysis technique. A debugger can stop execution at chosen points, inspect variables and registers, read process memory, and modify control flow. On iOS, debugging release apps usually involves the mechanisms described in @MASTG-TECH-0084, such as LLDB, `debugserver`, Mach task ports, and app entitlements.

Anti-debugging techniques on iOS can be grouped into two broad categories:

- **Preventive techniques** stop or disrupt debugger attachment.
- **Reactive techniques** inspect process state and let the app change behavior when a debugger is present.

## Using ptrace

The iOS XNU kernel implements the [`ptrace`](https://developer.apple.com/library/archive/documentation/System/Conceptual/ManPages_iPhoneOS/man2/ptrace.2.html "PTRACE(2)") system call. As described in @MASTG-TECH-0084, iOS debuggers use `ptrace` for operations such as attaching, stepping, and continuing execution, while memory and register access rely on Mach APIs and task ports.

The `ptrace` implementation includes `PT_DENY_ATTACH`, a request made by the traced process itself. Apple's `ptrace(2)` manual describes this request as a way for a process that is not currently traced to deny future tracing attempts. If a process is already traced when it makes the request, the process exits. If the request succeeds, later tracing attempts fail.

Because `ptrace` is not part of the public iOS SDK, implementations may resolve it dynamically with `dlsym` instead of importing it directly. Static analysis can therefore surface either direct `ptrace` references, `PT_DENY_ATTACH` constants, or string artifacts such as `ptrace`.

```c
#define PT_DENY_ATTACH 31

typedef int (*ptrace_ptr_t)(int request, pid_t pid, caddr_t addr, int data);

ptrace_ptr_t ptrace_ptr = (ptrace_ptr_t)dlsym(RTLD_DEFAULT, "ptrace");
ptrace_ptr(PT_DENY_ATTACH, 0, 0, 0);
```

!!! warning

    Using non-public APIs such as `ptrace` can cause App Store review issues. Consider whether your app's threat model and distribution requirements justify the added complexity and potential compatibility issues before implementing anti-debugging checks. See @MASVS-RESILIENCE for more information on the MASVS-RESILIENCE category and the associated guidelines.

## Using sysctl

The `sysctl` interface can retrieve kernel and process information. Apple's archived technical Q&A ["Detecting the Debugger"](https://developer.apple.com/library/archive/qa/qa1361/_index.html "Detecting the Debugger") shows a debug-build-oriented example that queries the current process with `sysctl` and checks the `P_TRACED` flag in `info.kp_proc.p_flag`.

The presence of a `sysctl` call alone does not prove anti-debugging behavior because apps can use it for other runtime information, such as device properties. Anti-debugging implementations usually combine `sysctl` with process-related Management Information Base values, `KERN_PROC_PID`, or checks for `P_TRACED`.

```c
int mib[] = {CTL_KERN, KERN_PROC, KERN_PROC_PID, getpid()};
struct kinfo_proc info;
size_t size = sizeof(info);

memset(&info, 0, sizeof(info));
sysctl(mib, 4, &info, &size, NULL, 0);

bool debugger_present = (info.kp_proc.p_flag & P_TRACED) != 0;
```

## Using getppid

Some implementations inspect the parent process ID with `getppid`. iOS apps are normally launched by system launch services, historically through `launchd` with PID 1. When a debugger launches or controls the app, the observed parent process can differ from the expected launcher process. This makes parent-process checks a possible reactive signal.

```c
pid_t parent_pid = getppid();
bool unexpected_parent = parent_pid != 1;
```

## Using Mach Exception Ports

Debuggers need to receive process events such as breakpoints and single-step exceptions. On Darwin-based systems, these events are delivered through Mach exception ports. An app can query its own registered exception ports with `task_get_exception_ports` and inspect whether a port is registered for breakpoint-related exceptions, for example with `EXC_MASK_BREAKPOINT`.

A non-null exception port returned for `EXC_MASK_BREAKPOINT` can indicate that debugger infrastructure such as LLDB and `debugserver` is attached to the process. A breakpoint exception port is a supporting signal rather than standalone proof, since other instrumentation can also interact with Mach exception ports, and an attacker can bypass this check by hooking `task_get_exception_ports`, changing the returned values, or running the check before attaching the debugger.

```c
exception_mask_t masks[EXC_TYPES_COUNT] = {0};
mach_port_t ports[EXC_TYPES_COUNT] = {0};
exception_behavior_t behaviors[EXC_TYPES_COUNT] = {0};
thread_state_flavor_t flavors[EXC_TYPES_COUNT] = {0};
mach_msg_type_number_t count = EXC_TYPES_COUNT;

kern_return_t kr = task_get_exception_ports(
    mach_task_self(),
    EXC_MASK_BREAKPOINT,
    masks,
    &count,
    ports,
    behaviors,
    flavors
);

bool breakpoint_port_present =
    kr == KERN_SUCCESS && count > 0 && ports[0] != MACH_PORT_NULL;
```

Because low-level APIs used for anti-debugging may be resolved dynamically, static analysis can also surface indirect lookup artifacts such as `dlsym` and string references to API names.
