//-------------------------------------------------------------------------------
// ============LICENSE_START=======================================================
//  Copyright (C) 2016-2018 Ericsson. All rights reserved.
// ================================================================================
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//      http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// 
// SPDX-License-Identifier: Apache-2.0
// ============LICENSE_END=========================================================
//-------------------------------------------------------------------------------

grammar ParametrizedType;

@header {
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
}

type returns[ClassBuilder value]
    : cls=CLASS          { $value = ClassBuilder.parse($cls.text); }
    | cls=CLASS          { $value = ClassBuilder.parse($cls.text); }
      LT head=type       { $value.add($head.value); }
        (COMMA tail=type { $value.add($tail.value); })* GT
    ;

GT  : '>'
    ;

LT  : '<'
    ;

COMMA
    : ','
    ;

CLASS
    : ('a'..'z'|'A'..'Z') ('a'..'z'|'A'..'Z'|'0'..'9'|'$'|'.'|'_')*
    ;
