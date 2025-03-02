/*
 * Copyright 2018-2019 Expedia Group, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.expedia.adaptivealerting.modelservice.web;

import com.expedia.adaptivealerting.anomdetect.detect.outlier.OutlierDetectorResult;
import com.expedia.adaptivealerting.modelservice.service.AnomalyRequest;
import com.expedia.adaptivealerting.modelservice.service.AnomalyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api")
public class AnomalyController {

    @Autowired
    private AnomalyService anomalyService;

    @PostMapping(path = "/anomalies", consumes = "application/json", produces = "application/json")
    public List<OutlierDetectorResult> getAnomalies(@RequestBody AnomalyRequest request) {
        return anomalyService.getAnomalies(request);
    }
}
