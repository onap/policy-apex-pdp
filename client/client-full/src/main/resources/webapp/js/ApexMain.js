/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

$(document).ready(function() {
    var widthIncrease = 30;
    var slideEaseTime = 300;
    var hoverIncreaseTime = 50;
    $(".placeholder").fadeIn("slow");
    $(".banner").each(function(i) {
        var width = $(this).width();
        $(this).delay(i * 250).animate({
            'opacity' : 1,
            "margin-left" : "15px"
        }, slideEaseTime, function() {
            $(this).hover(function() {
                $(this).stop(true, false).animate({
                    "width" : width + widthIncrease
                }, hoverIncreaseTime);
            }, function() {
                $(this).stop(true, false).animate({
                    "width" : width
                }, hoverIncreaseTime);
            });
        })
    })
});