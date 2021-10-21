/*******************************************************************************
 *  ============LICENSE_START=======================================================
 *  org.onap.dmaap
 *  ================================================================================
 *  Copyright © 2021 Orange Intellectual Property. All rights reserved.
 *  ================================================================================
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  ============LICENSE_END=========================================================
 *
 *  ECOMP is a trademark and service mark of AT&T Intellectual Property.
 *
 *******************************************************************************/
package com.onap.mrproxy.exception.handler;
import com.onap.mrproxy.exception.TopicNotFoundException;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ IOException.class, InterruptedException.class })
    public ResponseEntity<String> handlerIOException(Exception ex) {

        //TODO Below are 3 examples of how to handle exception types. Useful for learning , but please remove two before committing
//        return handleExceptionInternal(ex, "New Exception Body to align with MR",
//            new HttpHeaders(), HttpStatus.NOT_FOUND, null);
//
//        return new ResponseEntity("New Exception Body to align with MR", new HttpHeaders(), HttpStatus.NOT_FOUND);

        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler({ TopicNotFoundException.class })
    public ResponseEntity<String> handlerTopicNotFoundException(TopicNotFoundException ex) {

        return new ResponseEntity<>("New Exception Body to align with MR", new HttpHeaders(), HttpStatus.NOT_FOUND);

    }

}
