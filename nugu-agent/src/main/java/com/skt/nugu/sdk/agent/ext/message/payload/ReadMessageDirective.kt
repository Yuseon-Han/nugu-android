/**
 * Copyright (c) 2020 SK Telecom Co., Ltd. All rights reserved.
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

package com.skt.nugu.sdk.agent.ext.message.payload

import com.google.gson.annotations.SerializedName
import com.skt.nugu.sdk.agent.payload.PlayStackControl
import com.skt.nugu.sdk.core.interfaces.message.Header

data class ReadMessageDirective(
    @SerializedName("header")
    val header: Header,
    @SerializedName("payload")
    val payload: Payload
) {
    data class Payload(
        @SerializedName("playServiceId")
        val playServiceId: String,
        @SerializedName("token")
        val token: String,
        @SerializedName("receivedTime")
        val receivedTime: String?,
        @SerializedName("playStackControl")
        val playStackControl: PlayStackControl?
    )
}