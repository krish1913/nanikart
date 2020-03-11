/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.hello;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.wso2.siddhi.core.ExecutionPlanRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.query.output.callback.QueryCallback;
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.core.util.EventPrinter;

/**
 * Test for hello function.
 */
public class helloTestCase {
    // conversion equivalencies
    private static final double KILOMETERS_PER_MILE = 1.609344;
    private static Logger logger = Logger.getLogger(helloTestCase.class);
    protected static SiddhiManager siddhiManager;

    @Test
    public void testProcess() throws Exception {
        logger.info("helloFunctionExtension TestCase");
        siddhiManager = new SiddhiManager();
        String executionPlan = "define stream helloStream (inValue int); ";
        String eventFuseExecutionPlan = ("@info(name = 'query1') from helloStream "
                + " select hello:helloFunction(inValue) "
                + "as helloValue "
                + " insert into OutMediationStream;");
        ExecutionPlanRuntime executionPlanRuntime = siddhiManager
            .createExecutionPlanRuntime(executionPlan + eventFuseExecutionPlan);
        executionPlanRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents,
                                Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                Double result;
                for (Event event : inEvents) {
                    result = (Double) event.getData(0);
                    Assert.assertEquals((Double) 62.13711922373339, result);
                }
            }
        });
        InputHandler inputHandler = executionPlanRuntime.getInputHandler("helloStream");
        executionPlanRuntime.start();
        inputHandler.send(new Object[]{100});
        Thread.sleep(100);
        executionPlanRuntime.shutdown();
    }
}