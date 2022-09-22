// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.rules.core.ktlint

import com.pinterest.ktlint.core.api.EditorConfigProperties
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.ec4j.core.model.Property
import org.junit.jupiter.api.Test

class KtlintComposeKtConfigTest {
    private val mapping = mutableMapOf<String, Property>().apply {
        put("my_int", "10".prop)
        put("my_string", "abcd".prop)
        put("my_list", "a,b,c,a".prop)
        put("my_list2", "a , b , c,a".prop)
        put("my_set", "a,b,c,a,b,c".prop)
        put("my_set2", "  a, b,c ,a  , b  ,  c ".prop)
    }

    private val properties: EditorConfigProperties = mapping
    private val config = KtlintComposeKtConfig(properties)

    @Test
    fun `returns ints from properties, and default values when unset`() {
        assertThat(config.getInt("myInt", 0)).isEqualTo(10)
        assertThat(config.getInt("myOtherInt", 0)).isEqualTo(0)
    }

    @Test
    fun `returns strings from properties, and default values when unset`() {
        assertThat(config.getString("myString", null)).isEqualTo("abcd")
        assertThat(config.getString("myOtherString", "ABCD")).isEqualTo("ABCD")
        assertThat(config.getString("myOtherStringWithNullDefault", null)).isNull()
    }

    @Test
    fun `returns lists from properties, and default values when unset`() {
        assertThat(config.getList("myList", emptyList())).containsExactly("a", "b", "c", "a")
        assertThat(config.getList("myList2", emptyList())).containsExactly("a", "b", "c", "a")
        assertThat(config.getList("myOtherList", listOf("a"))).containsExactly("a")
    }

    @Test
    fun `returns sets from properties, and default values when unset`() {
        assertThat(config.getSet("mySet", emptySet())).containsExactly("a", "b", "c")
        assertThat(config.getSet("mySet2", emptySet())).containsExactly("a", "b", "c")
        assertThat(config.getSet("myOtherSet", setOf("a"))).containsExactly("a")
    }

    @Test
    fun `results are memoized`() {
        assertThat(config.getInt("myInt", 0)).isEqualTo(10)
        assertThat(config.getString("myString", null)).isEqualTo("abcd")
        assertThat(config.getList("myList", emptyList())).containsExactly("a", "b", "c", "a")
        assertThat(config.getList("myList2", emptyList())).containsExactly("a", "b", "c", "a")
        assertThat(config.getSet("mySet", emptySet())).containsExactly("a", "b", "c")
        assertThat(config.getSet("mySet2", emptySet())).containsExactly("a", "b", "c")

        mapping["my_int"] = "100".prop
        mapping["my_string"] = "XYZ".prop
        mapping["my_list"] = "z,y,x".prop
        mapping["my_list2"] = "z,y".prop
        mapping["my_set"] = "a".prop
        mapping["my_set2"] = "a, b".prop

        assertThat(config.getInt("myInt", 0)).isEqualTo(10)
        assertThat(config.getString("myString", null)).isEqualTo("abcd")
        assertThat(config.getList("myList", emptyList())).containsExactly("a", "b", "c", "a")
        assertThat(config.getList("myList2", emptyList())).containsExactly("a", "b", "c", "a")
        assertThat(config.getSet("mySet", emptySet())).containsExactly("a", "b", "c")
        assertThat(config.getSet("mySet2", emptySet())).containsExactly("a", "b", "c")
    }

    private val String.prop: Property
        get() = Property.builder().value(this).build()
}
