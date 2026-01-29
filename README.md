# JUnit Parallelization Recipes

[Parallel execution](https://docs.junit.org/6.0.2/writing-tests/parallel-execution.html)
of JUnit tests can significantly speed up test execution and therefore the whole feedback
cycle.

For parallel execution one must make sure that tests are truly isolated from each other.
This is easy to achieve for pure functions without any complex dependencies. Things become
more complicated if you need external dependencies like database or services. Even a
mocking framework like [mockk](https://mockk.io/) can break test isolation.

This repository provides solutions for those challenges in the form of recipes, that can
be copied to your own project.

# Disclaimer

The recipes are focused on the technology stack I use at work. Those are:

- JUnit
- AssertJ
- Spring Boot
- MongoDB
- AWS services
- [mockk](https://mockk.io/)

# Problems

## Spring Context Cache

The Spring test framework caches contexts and uses a single cached version in multiple
parallel running test cases. If one test case changes some global shared state, like
a test container database, it can easily break other test cases. Possible solutions are

- Write tests in a way that they can handle changes in shared state. This might work
  for everything that is request scoped. But things like feature toggles won't work
  due to them being not isolated by design.
- Add the `@Isolated` JUnit annotation to affected tests. But this will prevent any
  test with this annotation to run in parallel. Even tests that have different test
  contexts.
- Use the [JUnit synchronization](https://docs.junit.org/6.0.1/writing-tests/parallel-execution.html#synchronization)
  extension to prevent two tests that use the same Spring context to run concurrently.
  This approach is implemented in the SpringContextResourceLocksProvider. The
  SharedMutableStateTest.kt file shows the usage of the lock provider. If there are
  only few different contexts, this approach can still reduce parallelism.
