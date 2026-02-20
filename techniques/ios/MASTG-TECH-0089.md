---
title: Symbolic Execution
platform: ios
---

TODO: Currently don't have this, but we do have this section below, which does talk about Angr and a small intro referring to Android

You can find an introduction to binary analysis using binary analysis frameworks in @MASTG-TECH-0015. We recommend revisiting that content and refreshing your understanding of the concepts in this area.

For Android, we used Angr's symbolic execution engine to solve a challenge. In this section, we will first use Unicorn to solve the @MASTG-APP-0025 challenge, and then revisit the Angr binary analysis framework to analyze it. Instead of symbolic execution, we will use its concrete execution (or dynamic execution) features.

## Angr

@MASTG-TOOL-0030 is a versatile tool that provides multiple techniques to facilitate binary analysis and supports various file formats and hardware instruction sets.

> The Mach-O backend in Angr is not well supported, but it works perfectly fine for our case.

While manually analyzing the code in @MASTG-TECH-0077, we reached a point where performing further manual analysis was cumbersome. The function at offset `0x1000080d4` was identified as the final target, which contains the secret string.

If we revisit that function, we can see that it involves multiple sub-function calls, and, interestingly, none of these functions depend on other library or system calls. This is a perfect case to use Angr's concrete execution engine. Follow the steps below to solve this challenge:

- Get the ARM64 version of the binary by running `lipo -thin arm64 <app_binary> -output uncrackable.arm64` (ARMv7 can be used as well).
- Create an Angr `Project` by loading the above binary.
- Get a `callable` object by passing the address of the function to be executed. From the Angr documentation: "A Callable is a representation of a function in the binary that can be interacted with like a native Python function."
- Pass the above `callable` object to the concrete execution engine, which in this case is `claripy.backends.concrete`.
- Access the memory and extract the string from the pointer returned by the above function.

```python
import angr
import claripy

def solve():

    # Load the binary by creating angr project.
    project = angr.Project('uncrackable.arm64')

    # Pass the address of the function to the callable
    func = project.factory.callable(0x1000080d4)

    # Get the return value of the function
    ptr_secret_string = claripy.backends.concrete.convert(func()).value
    print("Address of the pointer to the secret string: " + hex(ptr_secret_string))

    # Extract the value from the pointer to the secret string
    secret_string = func.result_state.mem[ptr_secret_string].string.concrete
    print(f"Secret String: {secret_string}")

solve()
```

Above, Angr executed an ARM64 code in an execution environment provided by one of its concrete execution engines. The result is retrieved from memory, as if the program were executed on a real device. This case is a good example of how binary analysis frameworks enable us to perform a comprehensive analysis of a binary, even without specialized hardware to run it.
