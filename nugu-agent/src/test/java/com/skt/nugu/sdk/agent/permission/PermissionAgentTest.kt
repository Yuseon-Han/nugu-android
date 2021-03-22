/**
 * Copyright (c) 2021 SK Telecom Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http:www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.skt.nugu.sdk.agent.permission

import com.nhaarman.mockito_kotlin.*
import com.skt.nugu.sdk.core.interfaces.common.NamespaceAndName
import com.skt.nugu.sdk.core.interfaces.context.ContextManagerInterface
import com.skt.nugu.sdk.core.interfaces.context.ContextSetterInterface
import com.skt.nugu.sdk.core.interfaces.context.ContextType
import com.skt.nugu.sdk.core.interfaces.context.StateRefreshPolicy
import com.skt.nugu.sdk.core.interfaces.message.Header
import org.junit.Assert
import org.junit.Test
import java.util.HashMap
import java.util.concurrent.Executors

class PermissionAgentTest {
    @Test
    fun testRegisteredAtContextManager() {
        val contextManager: ContextManagerInterface = mock()
        val agent = PermissionAgent(contextManager, mock())
        verify(contextManager).setStateProvider(agent.namespaceAndName, agent)
    }

    @Test
    fun testGetInterfaceName() {
        val agent = PermissionAgent(mock(), mock())
        Assert.assertTrue(agent.getInterfaceName() == PermissionAgent.NAMESPACE)
    }

    @Test
    fun testProvideStateWithCompactContext() {
        val agent = PermissionAgent(mock(), mock())
        val contextSetter: ContextSetterInterface = mock()

        val namespaceAndName = NamespaceAndName("", "")
        val token = 1

        agent.provideState(contextSetter, namespaceAndName, ContextType.COMPACT, token)

        Executors.newSingleThreadExecutor().submit {
            verify(contextSetter, times(1)).setState(
                namespaceAndName,
                PermissionAgent.StateContext.CompactContextState,
                StateRefreshPolicy.NEVER,
                ContextType.COMPACT,
                token
            )
        }.get()
    }

    @Test
    fun testProvideStateWithFullContext() {
        val delegate: PermissionDelegate = mock()
        whenever(delegate.supportedPermissions).thenReturn(arrayOf(PermissionType.LOCATION))
        whenever(delegate.getPermissionState(PermissionType.LOCATION)).thenReturn(PermissionState.DENIED)

        val agent = PermissionAgent(mock(), delegate)
        val contextSetter: ContextSetterInterface = mock()

        val namespaceAndName = NamespaceAndName("", "")
        val token = 1

        agent.provideState(contextSetter, namespaceAndName, ContextType.FULL, token)

        Executors.newSingleThreadExecutor().submit {
            verify(contextSetter, atLeastOnce()).setState(
                namespaceAndName,
                PermissionAgent.StateContext(HashMap<PermissionType, PermissionState>().apply {
                    delegate.supportedPermissions.forEach {
                        put(it, delegate.getPermissionState(it))
                    }
                }),
                StateRefreshPolicy.ALWAYS,
                ContextType.FULL,
                token
            )
        }.get()
    }

    @Test
    fun testRequestPermission() {
        val delegate: PermissionDelegate = mock()
        val agent = PermissionAgent(mock(), delegate)
        val payload: RequestPermissionDirectiveHandler.Payload =
            RequestPermissionDirectiveHandler.Payload(
                "playServiceId",
                arrayOf(PermissionType.LOCATION)
            )

        agent.requestPermission(Header("", "", "", "", "", ""), payload)

        Executors.newSingleThreadExecutor().submit {
            verify(delegate, times(1)).requestPermissions(payload.permissions)
        }.get()
    }
}