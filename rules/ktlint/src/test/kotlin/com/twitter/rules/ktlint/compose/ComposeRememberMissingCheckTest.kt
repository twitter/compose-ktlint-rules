// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.rules.ktlint.compose

import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThat
import com.pinterest.ktlint.test.LintViolation
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class ComposeRememberMissingCheckTest {

    private val rememberRuleAssertThat = ComposeRememberMissingCheck().assertThat()

    @Test
    fun `passes when a non-remembered mutableStateOf is used outside of a Composable`() {
        @Language("kotlin")
        val code =
            """
                val msof = mutableStateOf("X")
            """.trimIndent()
        rememberRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `errors when a non-remembered mutableStateOf is used in a Composable`() {
        @Language("kotlin")
        val code =
            """
                @Composable
                fun MyComposable() {
                    val something = mutableStateOf("X")
                }
                @Composable
                fun MyComposable(something: State<String> = mutableStateOf("X")) {
                }
            """.trimIndent()
        rememberRuleAssertThat(code).hasLintViolationsWithoutAutoCorrect(
            LintViolation(
                line = 3,
                col = 21,
                detail = ComposeRememberMissingCheck.MutableStateOfNotRemembered
            ),
            LintViolation(
                line = 6,
                col = 45,
                detail = ComposeRememberMissingCheck.MutableStateOfNotRemembered
            )
        )
    }

    @Test
    fun `passes when a remembered mutableStateOf is used in a Composable`() {
        @Language("kotlin")
        val code =
            """
                @Composable
                fun MyComposable(
                    something: State<String> = remember { mutableStateOf("X") }
                ) {
                    val something = remember { mutableStateOf("X") }
                    val something2 by remember { mutableStateOf("Y") }
                }
            """.trimIndent()
        rememberRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `passes when a rememberSaveable mutableStateOf is used in a Composable`() {
        @Language("kotlin")
        val code =
            """
                @Composable
                fun MyComposable(
                    something: State<String> = rememberSaveable { mutableStateOf("X") }
                ) {
                    val something = rememberSaveable { mutableStateOf("X") }
                    val something2 by rememberSaveable { mutableStateOf("Y") }
                }
            """.trimIndent()
        rememberRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `passes when a non-remembered derivedStateOf is used outside of a Composable`() {
        @Language("kotlin")
        val code =
            """
                val dsof = derivedStateOf("X")
            """.trimIndent()
        rememberRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `errors when a non-remembered derivedStateOf is used in a Composable`() {
        @Language("kotlin")
        val code =
            """
                @Composable
                fun MyComposable() {
                    val something = derivedStateOf { "X" }
                }
                @Composable
                fun MyComposable(something: State<String> = derivedStateOf { "X" }) {
                }
            """.trimIndent()
        rememberRuleAssertThat(code).hasLintViolationsWithoutAutoCorrect(
            LintViolation(
                line = 3,
                col = 21,
                detail = ComposeRememberMissingCheck.DerivedStateOfNotRemembered
            ),
            LintViolation(
                line = 6,
                col = 45,
                detail = ComposeRememberMissingCheck.DerivedStateOfNotRemembered
            )
        )
    }

    @Test
    fun `passes when a remembered derivedStateOf is used in a Composable`() {
        @Language("kotlin")
        val code =
            """
                @Composable
                fun MyComposable(
                    something: State<String> = remember { derivedStateOf { "X" } }
                ) {
                    val something = remember { derivedStateOf { "X" } }
                    val something2 by remember { derivedStateOf { "Y" } }
                }
            """.trimIndent()
        rememberRuleAssertThat(code).hasNoLintViolations()
    }
}
