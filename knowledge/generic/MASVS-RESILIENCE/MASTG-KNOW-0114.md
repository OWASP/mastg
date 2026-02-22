---
masvs_category: MASVS-RESILIENCE
platform: generic
title: Debugging and Tracing
---

In the traditional sense, debugging is the process of identifying and isolating problems in a program as part of the software development life cycle. The same tools used for debugging are valuable to reverse engineers even when identifying bugs is not the primary goal. Debuggers enable program suspension at any point during runtime, inspection of the process' internal state, and even register and memory modification. These abilities simplify program inspection.

Tracing refers to passive logging of information about the app's execution (such as API calls). Tracing can be done in several ways, including debugging APIs, function hooks, and Kernel tracing facilities. See ["Anatomy of a code tracer"](https://medium.com/@oleavr/anatomy-of-a-code-tracer-b081aadb0df8) by Ole André Vadla Ravnås for more details.
