// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.detekt

import com.twitter.compose.rules.ComposeNaming
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class ComposeNamingCheckTest {

    private val ruleReturns = ComposeNamingReturnResultsCheck(Config.empty)
    private val ruleNotReturns = ComposeNamingDontReturnResultsCheck(Config.empty)

    @Test
    fun `passes when a composable that returns values is lowercase`() {
        @Language("kotlin")
        val code =
            """
                @Composable
                fun myComposable(): Something { }
            """.trimIndent()
        assertThat(ruleReturns.lint(code)).isEmpty()
        assertThat(ruleNotReturns.lint(code)).isEmpty()
    }

    @Test
    fun `passes when a composable that returns nothing or Unit is uppercase`() {
        @Language("kotlin")
        val code =
            """
                @Composable
                fun MyComposable() { }
                @Composable
                fun MyComposable(): Unit { }
            """.trimIndent()
        assertThat(ruleReturns.lint(code)).isEmpty()
        assertThat(ruleNotReturns.lint(code)).isEmpty()
    }

    @Test
    fun `passes when a composable doesn't have a body block, is a property or a lambda`() {
        @Language("kotlin")
        val code =
            """
                @Composable
                fun MyComposable() = Text("bleh")

                val composable: Something
                    @Composable get() { }

                val composable: Something
                    @Composable get() = OtherComposable()

                val whatever = @Composable { }
            """.trimIndent()
        assertThat(ruleReturns.lint(code)).isEmpty()
        assertThat(ruleNotReturns.lint(code)).isEmpty()
    }

    @Test
    fun `errors when a composable returns a value and is capitalized`() {
        @Language("kotlin")
        val code =
            """
                @Composable
                fun MyComposable(): Something { }
            """.trimIndent()
        val errors = ruleReturns.lint(code)
        assertThat(errors).hasSize(1)
            .hasSourceLocations(
                SourceLocation(2, 5)
            )
        assertThat(errors.first()).hasMessage(ComposeNaming.ComposablesThatReturnResultsShouldBeLowercase)
    }

    @Test
    fun `errors when a composable returns nothing or Unit and is lowercase`() {
        @Language("kotlin")
        val code =
            """
                @Composable
                fun myComposable() { }

                @Composable
                fun myComposable(): Unit { }
            """.trimIndent()

        val errors = ruleNotReturns.lint(code)
        assertThat(errors).hasSize(2)
            .hasSourceLocations(
                SourceLocation(2, 5),
                SourceLocation(5, 5)
            )
        for (error in errors) {
            assertThat(error).hasMessage(ComposeNaming.ComposablesThatDoNotReturnResultsShouldBeCapitalized)
        }
    }
}
