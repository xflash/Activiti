/* Licensed under the Apache License, Version 2.0 (the "License");
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
 */
package org.activiti.engine.impl.webservice;

import org.activiti.engine.impl.bpmn.webservice.MessageDefinition;
import org.activiti.engine.impl.bpmn.webservice.MessageInstance;
import org.activiti.engine.impl.bpmn.webservice.Operation;
import org.activiti.engine.impl.bpmn.webservice.OperationImplementation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a WS implementation of a {@link Operation}
 * 
 * @author Esteban Robles Luna
 */
public class WSOperation implements OperationImplementation {

  private static final Logger LOGGER = LoggerFactory.getLogger(WSOperation.class);
  
  protected String id;

  protected String name;
  
  protected WSService service;
  
  public WSOperation(String id, String operationName, WSService service) {
    this.id = id;
    this.name = operationName;
    this.service = service;
  }
  
  /**
   * {@inheritDoc}
   */
  public String getId() {
    return this.id;
  }
  
  /**
   * {@inheritDoc}
   */
  public String getName() {
    return this.name;
  }

  /**
   * {@inheritDoc}
   */
  public MessageInstance sendFor(MessageInstance message, Operation operation) {
    Object[] arguments = this.getArguments(message);
    Object[] results = this.safeSend(arguments);
    return this.createResponseMessage(results, operation);
  }

  private Object[] getArguments(MessageInstance message) {
    return message.getStructureInstance().toArray();
  }
  
  private Object[] safeSend(Object[] arguments) {
    Object[] results = null;
    
    try {
      results = this.service.getClient().send(this.name, arguments);
    } catch (Exception e) {
      LOGGER.warn("Error calling WS {}", this.service.getName(), e);
    }
    
    if (results == null) {
      results = new Object[] {};
    }
    return results;
  }
  
  private MessageInstance createResponseMessage(Object[] results, Operation operation) {
    MessageInstance message = null;
    MessageDefinition outMessage = operation.getOutMessage();
    if (outMessage != null) {
      message = outMessage.createInstance();
      message.getStructureInstance().loadFrom(results);
    }
    return message;
  }

  public WSService getService() {
    return this.service;
  }
}
