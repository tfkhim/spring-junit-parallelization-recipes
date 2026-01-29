package com.github.tfkhim.junitParallelizationRecepies.springContextCaching

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.ResourceLock
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.toJavaDuration

/*
 * Removing the @ResourceLock annotations from the test class will make
 * all but two (one per context) tests fail.
 */

@ResourceLock(providers = [SpringContextResourceLocksProvider::class])
@SpringBootTest(classes = [SharedMutableState::class])
class FirstSharedMutableStateTest {
    @Autowired
    private lateinit var state: SharedMutableState

    @Test
    fun `first class`() {
        log.info("First test runs on thread: ${Thread.currentThread().name}")

        val newValue = UUID.randomUUID()

        val changedValue = state.changeState(newValue)

        assertThat(changedValue).isEqualTo(newValue)
    }

    @Nested
    inner class NestedWithDifferentSpringContextTest {
        @Autowired
        private lateinit var state: SharedMutableState

        @Test
        fun `nested class`() {
            log.info("Nested test runs on thread: ${Thread.currentThread().name}")

            val newValue = UUID.randomUUID()

            val changedValue = state.changeState(newValue)

            assertThat(changedValue).isEqualTo(newValue)
        }
    }
}

@ResourceLock(providers = [SpringContextResourceLocksProvider::class])
@SpringBootTest(classes = [SharedMutableState::class])
class SecondSharedMutableStateTest {
    @Autowired
    private lateinit var state: SharedMutableState

    @Test
    fun `second class`() {
        log.info("Second test runs on thread: ${Thread.currentThread().name}")

        val newValue = UUID.randomUUID()

        val changedValue = state.changeState(newValue)

        assertThat(changedValue).isEqualTo(newValue)
    }
}

@ResourceLock(providers = [SpringContextResourceLocksProvider::class])
@SpringBootTest(classes = [SharedMutableState::class, AdditionalBean::class])
class DifferentSpringContextTest {
    @Autowired
    private lateinit var state: SharedMutableState

    @RepeatedTest(5)
    fun `different context`() {
        log.info("Test with different context runs on thread: ${Thread.currentThread().name}")

        val newValue = UUID.randomUUID()

        val changedValue = state.changeState(newValue)

        assertThat(changedValue).isEqualTo(newValue)
    }
}

class SharedMutableState {
    private var value: UUID = UUID.randomUUID()

    fun changeState(newValue: UUID): UUID {
        value = newValue
        Thread.sleep(100.milliseconds.toJavaDuration())
        return value
    }
}

class AdditionalBean

private val log = LoggerFactory.getLogger("SharedMutableStateTest")
