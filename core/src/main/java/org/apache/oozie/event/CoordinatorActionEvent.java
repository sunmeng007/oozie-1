/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.oozie.event;

import java.util.Date;

import org.apache.oozie.AppType;
import org.apache.oozie.client.CoordinatorAction;
import org.apache.oozie.client.event.JobEvent;
import org.apache.oozie.service.EventHandlerService;
import org.apache.oozie.util.XLog;

/**
 * Class implementing JobEvent for events generated by Coordinator Actions
 */
@SuppressWarnings("serial")
public class CoordinatorActionEvent extends JobEvent {

    private CoordinatorAction.Status status;
    private Date nominalTime;
    private String missingDeps;
    private String errorCode;
    private String errorMessage;
    // TODO more attributes - frequency, timeunit, bundleName
    // for some advanced processing and linking using events

    public CoordinatorActionEvent(String id, String parentId, CoordinatorAction.Status status, String user,
            String appName, Date nomTime, Date startTime, String missDeps) {
        super(id, parentId, user, AppType.COORDINATOR_ACTION, appName);
        setStatus(status);
        setNominalTime(nomTime);
        setStartTime(startTime);
        setMissingDeps(missDeps);
        XLog.getLog(EventHandlerService.class).trace("Event generated - " + this.toString());
    }

    public String getBundleJobId() {
        return null; // TODO extract prefix from bundleActionId before '@'
    }

    public CoordinatorAction.Status getStatus() {
        return status;
    }

    public void setStatus(CoordinatorAction.Status castatus) {
        status = castatus;
        // set high-level status for event based on low-level actual job status
        // this is to ease filtering on the consumer side
        switch (status) {
            case WAITING:
                setEventStatus(EventStatus.WAITING);
                break;
            case SUCCEEDED:
                setEventStatus(EventStatus.SUCCESS);
                setEndTime(new Date());
                break;
            case RUNNING:
                setEventStatus(EventStatus.STARTED);
                break;
            case SUSPENDED:
                setEventStatus(EventStatus.SUSPEND);
                break;
            case KILLED:
            case FAILED:
            case TIMEDOUT:
                setEventStatus(EventStatus.FAILURE);
                setEndTime(new Date());
        }
    }

    public Date getNominalTime() {
        return nominalTime;
    }

    public void setNominalTime(Date time) {
        nominalTime = time;
    }

    public String getMissingDeps() {
        return missingDeps;
    }

    public void setMissingDeps(String dependencies) {
        missingDeps = dependencies;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String code) {
        errorCode = code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String msg) {
        errorMessage = msg;
    }

}
