package com.jmvsta.suites

import com.jmvsta.scenarios.Scenario02
import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite

@Suite
@SelectClasses(Scenario02::class)
class TwoChats