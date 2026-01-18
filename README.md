# JUnit Parallelization Recipes

[Parallel execution](https://docs.junit.org/6.0.2/writing-tests/parallel-execution.html)
of JUnit tests can significantly speed up test execution and therefore the whole feedback
cycle.

For parallel execution one must make sure that tests are truely isolated from each other.
This is easy to achive for pure functions without any complex dependencies. Things become
more complicated if you need external dependencies like database or services. Even a
mocking framework like [mockk](https://mockk.io/) can break test isolation.

This respository provides solutions for those challenges in the form of recepis, that can
be copied to your own project.

