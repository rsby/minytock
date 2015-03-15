![http://minytock.googlecode.com/svn/images/simple_logo.png](http://minytock.googlecode.com/svn/images/simple_logo.png)

**_an ultra-tiny yet rich and high-performance mocking toolkit for java integration tests_**

minytock is really a delegation framework for dynamically delegating method calls from one object to another, and doing it quickly.

minytock will work with any unit testing framework and any java code, but was designed with junit and the spring framework in mind, and so there are a few extra pieces to help make minytock an ideal choice for quickly building _meaningful_ and _readable_ tests run with the SpringJUnit4ClassRunner.

### minytock manifesto ###
an integration mocking toolkit should have an emphasis on a small API, support for parallel testing, readability through method-chaining when possible, should not be adversely affected by the number of calls to mocks, should support dynamic mocking, should integrate well with your IoC container, and should have a minimal impact on processing time and system memory.

### why not use a unit-testing toolkit? ###
minytock is designed to handle high loads quickly.  For 1 million calls to a void, empty method, minytock is around 500 times faster and uses considerably less memory than the unit-testing toolkit jmockit. for a single call to a one-shot mock, minytock is about 10 times faster than jmockit.  minytock also allows safely mocking the same service in different threads to support running tests concurrently - a big gain if your tests take hours to run.