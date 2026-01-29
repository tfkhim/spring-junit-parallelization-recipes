package com.github.tfkhim.junitParallelizationRecepies.springContextCaching

import org.junit.jupiter.api.parallel.ResourceAccessMode
import org.junit.jupiter.api.parallel.ResourceLocksProvider
import org.springframework.test.context.BootstrapUtils
import org.springframework.test.context.MergedContextConfiguration
import java.lang.reflect.Method
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class SpringContextResourceLocksProvider : ResourceLocksProvider {
    override fun provideForMethod(
        enclosingInstanceTypes: List<Class<*>>,
        testClass: Class<*>,
        testMethod: Method,
    ): Set<ResourceLocksProvider.Lock> {
        val bootstrapper = BootstrapUtils.resolveTestContextBootstrapper(testClass)
        val mergedConfig = bootstrapper.buildMergedContextConfiguration()
        val key = contextToKeyMap.computeIfAbsent(mergedConfig) { UUID.randomUUID().toString() }
        return setOf(ResourceLocksProvider.Lock(key, ResourceAccessMode.READ_WRITE))
    }

    companion object {
        private val contextToKeyMap = ConcurrentHashMap<MergedContextConfiguration, String>()
    }
}
