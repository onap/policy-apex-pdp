*** Settings ***
Library     Collections
Library     RequestsLibrary
Library     OperatingSystem
Library     json

*** Test Cases ***

Healthcheck
     [Documentation]    Runs Apex PDP Health check
     ${auth}=    Create List    healthcheck    zb!XztG34
     Log    Creating session https://${APEX_IP}:6969
     ${session}=    Create Session      policy  https://${APEX_IP}:6969   auth=${auth}
     ${headers}=  Create Dictionary     Accept=application/json    Content-Type=application/json
     ${resp}=   GET On Session     policy  /policy/apex-pdp/v1/healthcheck     headers=${headers}
     Log    Received response from policy ${resp.text}
     Should Be Equal As Strings    ${resp.status_code}     200
     Should Be Equal As Strings    ${resp.json()['code']}  200

ExecuteApexPolicy
     Wait Until Keyword Succeeds    2 min    5 sec    CreatePolicy
     Wait Until Keyword Succeeds    3 min    5 sec    VerifyPdpStatistics    0    0    0    0
     Wait Until Keyword Succeeds    2 min    5 sec    DeployPolicy
     Wait Until Keyword Succeeds    2 min    5 sec    VerifyPolicyStatus
     Wait Until Keyword Succeeds    3 min    5 sec    VerifyPdpStatistics    1    1    0    0
     Wait Until Keyword Succeeds    4 min    10 sec    RunEventOnApexEngine
     Wait Until Keyword Succeeds    3 min    5 sec    VerifyPdpStatistics    1    1    1    1

*** Keywords ***

CreatePolicy
     [Documentation]    Create a new Apex policy
     ${auth}=    Create List    healthcheck    zb!XztG34
     ${postjson}=  Get file  ${CURDIR}/data/onap.policies.native.Apex.tosca.json
     Log    Creating session https://${POLICY_API_IP}:6969
     ${session}=    Create Session      policy  https://${POLICY_API_IP}:6969   auth=${auth}
     ${headers}=  Create Dictionary     Accept=application/json    Content-Type=application/json
     ${resp}=   POST On Session   policy  /policy/api/v1/policytypes/onap.policies.native.Apex/versions/1.0.0/policies  data=${postjson}   headers=${headers}
     Log    Received response from policy ${resp.text}
     Should Be Equal As Strings    ${resp.status_code}     200
     Dictionary Should Contain Key    ${resp.json()}    tosca_definitions_version

DeployPolicy
     [Documentation]    Deploy the policy in apex-pdp engine
     ${auth}=    Create List    healthcheck    zb!XztG34
     ${postjson}=  Get file  ${CURDIR}/data/pdp_update.json
     Log    Creating session https://${POLICY_PAP_IP}:6969
     ${session}=    Create Session      policy  https://${POLICY_PAP_IP}:6969   auth=${auth}
     ${headers}=  Create Dictionary     Accept=application/json    Content-Type=application/json
     ${resp}=   POST On Session   policy  /policy/pap/v1/pdps/deployments/batch  data=${postjson}   headers=${headers}
     Log    Received response from policy ${resp.text}
     Should Be Equal As Strings    ${resp.status_code}     202

VerifyPolicyStatus
     [Documentation]    Verify policy deployment status
     ${auth}=    Create List    healthcheck    zb!XztG34
     Log    Creating session https://${POLICY_PAP_IP}:6969
     ${session}=    Create Session      policy  https://${POLICY_PAP_IP}:6969   auth=${auth}
     ${headers}=  Create Dictionary     Accept=application/json    Content-Type=application/json
     ${resp}=   GET On Session     policy  /policy/pap/v1/policies/status     headers=${headers}
     Log    Received response from policy ${resp.text}
     Should Be Equal As Strings    ${resp.status_code}     200
     Should Be Equal As Strings    ${resp.json()[0]['pdpGroup']}  defaultGroup
     Should Be Equal As Strings    ${resp.json()[0]['pdpType']}  apex
     Should Be Equal As Strings    ${resp.json()[0]['pdpId']}  policy-apex-pdp
     Should Be Equal As Strings    ${resp.json()[0]['policy']['name']}  onap.policies.native.apex.Sampledomain
     Should Be Equal As Strings    ${resp.json()[0]['policy']['version']}  1.0.0
     Should Be Equal As Strings    ${resp.json()[0]['policyType']['name']}  onap.policies.native.Apex
     Should Be Equal As Strings    ${resp.json()[0]['policyType']['version']}  1.0.0
     Should Be Equal As Strings    ${resp.json()[0]['deploy']}  True
     Should Be Equal As Strings    ${resp.json()[0]['state']}  SUCCESS

RunEventOnApexEngine
    [Documentation]    Send event to verify policy execution
    Create Session   apexSession  http://${APEX_IP}:23324   max_retries=1
    ${data}=    Get Binary File     ${CURDIR}${/}data${/}event.json
    &{headers}=  Create Dictionary    Content-Type=application/json    Accept=application/json
    ${resp}=    PUT On Session    apexSession    /apex/FirstConsumer/EventIn    data=${data}   headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}   200

VerifyPdpStatistics
     [Documentation]    Verify pdp statistics after policy execution
     [Arguments]    ${deployCount}    ${deploySuccessCount}    ${executedCount}    ${executedSuccessCount}
     ${auth}=    Create List    healthcheck    zb!XztG34
     Log    Creating session https://${POLICY_PAP_IP}:6969
     ${session}=    Create Session      policy  https://${POLICY_PAP_IP}:6969   auth=${auth}
     ${headers}=  Create Dictionary     Accept=application/json    Content-Type=application/json
     ${resp}=    GET On Session    policy    /policy/pap/v1/pdps/statistics/defaultGroup/apex/policy-apex-pdp    params=recordCount=1    headers=${headers}
     Log    Received response from policy ${resp.text}
     Should Be Equal As Strings    ${resp.status_code}     200
     Should Be Equal As Strings    ${resp.json()['defaultGroup']['apex'][0]['pdpInstanceId']}  policy-apex-pdp
     Should Be Equal As Strings    ${resp.json()['defaultGroup']['apex'][0]['pdpGroupName']}  defaultGroup
     Should Be Equal As Strings    ${resp.json()['defaultGroup']['apex'][0]['pdpSubGroupName']}  apex
     Should Be Equal As Strings    ${resp.json()['defaultGroup']['apex'][0]['policyDeployCount']}  ${deployCount}
     Should Be Equal As Strings    ${resp.json()['defaultGroup']['apex'][0]['policyDeploySuccessCount']}  ${deploySuccessCount}
     Should Be Equal As Strings    ${resp.json()['defaultGroup']['apex'][0]['policyDeployFailCount']}  0
     Should Be Equal As Strings    ${resp.json()['defaultGroup']['apex'][0]['policyExecutedCount']}  ${executedCount}
     Should Be Equal As Strings    ${resp.json()['defaultGroup']['apex'][0]['policyExecutedSuccessCount']}  ${executedSuccessCount}
     Should Be Equal As Strings    ${resp.json()['defaultGroup']['apex'][0]['policyExecutedFailCount']}  0
