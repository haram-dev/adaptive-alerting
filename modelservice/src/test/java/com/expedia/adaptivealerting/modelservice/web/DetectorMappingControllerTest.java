package com.expedia.adaptivealerting.modelservice.web;

import com.expedia.adaptivealerting.modelservice.dto.detectormapping.Detector;
import com.expedia.adaptivealerting.modelservice.dto.detectormapping.MatchingDetectorsResponse;
import com.expedia.adaptivealerting.modelservice.dto.detectormapping.SearchMappingsRequest;
import com.expedia.adaptivealerting.modelservice.dto.detectormapping.User;
import com.expedia.adaptivealerting.modelservice.entity.DetectorMapping;
import com.expedia.adaptivealerting.modelservice.service.DetectorMappingServiceImpl;
import lombok.val;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
@AutoConfigureMockMvc
public class DetectorMappingControllerTest {

    @Mock
    private DetectorMappingServiceImpl detectorMappingService;

    private String id = "adsvade8^szx";
    private String detectorUuid = "aeb4d849-847a-45c0-8312-dc0fcf22b639";
    private String userVal = "test-user";

    @InjectMocks
    private DetectorMappingController controllerUnderTest;

    @Before
    public void setUp() {
        this.controllerUnderTest = new DetectorMappingController();
        MockitoAnnotations.initMocks(this);
    }

    @Before
    public void beforeTest() {
        detectorMappingService = new DetectorMappingServiceImpl();
    }

    @Test
    public void testGetDetectorMappings_successful() throws IOException {
        DetectorMapping detectorMapping = mockDetectorMapping(id);
        when(detectorMappingService.findDetectorMapping(id)).thenReturn(detectorMapping);
        DetectorMapping detectorMappingreturned = controllerUnderTest.getDetectorMapping(id);
        assertNotNull("Response can't be null", detectorMappingreturned);
        assertEquals(UUID.fromString(detectorUuid), detectorMappingreturned.getDetector().getUuid());
        assertEquals(id, detectorMappingreturned.getId());
        assertEquals(userVal, detectorMapping.getUser().getId());
        assertEquals(true, detectorMapping.isEnabled());
    }

    @Test(expected = RuntimeException.class)
    public void testGetDetectorMappings_fail() throws IOException {
        when(detectorMappingService.findDetectorMapping(id)).thenReturn(new DetectorMapping());
        DetectorMapping detectorMappingreturned = controllerUnderTest.getDetectorMapping(id);
        assertNotNull("Response can't be null", detectorMappingreturned);
        assertEquals(detectorUuid, detectorMappingreturned.getDetector().getUuid());
        assertEquals(id, detectorMappingreturned.getId());
    }

    @Test
    @ResponseStatus(value = HttpStatus.OK)
    public void testDisableDetectorMappings() throws IOException {
        controllerUnderTest.disableDeleteDetectorMapping(id);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDisableDetectorMappings_notNull() throws IOException {
        controllerUnderTest.disableDeleteDetectorMapping(null);
    }

    @Test
    @ResponseStatus(value = HttpStatus.OK)
    public void testDeleteDetectorMappings() throws IOException {
        controllerUnderTest.deleteDetectorMapping(id);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDeleteDetectorMappings_notNull() throws IOException {
        controllerUnderTest.deleteDetectorMapping(null);
    }

    @Test
    public void testGetLastUpdated_successful() throws IOException {
        val timeInSecs = 60;
        List<DetectorMapping> mockeddetectorMappingsList = mockDetectorMappingsList();
        when(detectorMappingService.findLastUpdated(timeInSecs)).thenReturn(mockeddetectorMappingsList);
        List<DetectorMapping> listofdetectorMappingsreturned = controllerUnderTest.findDetectorMapping(timeInSecs);
        assertNotNull("Response can't be null", listofdetectorMappingsreturned);
        assertEquals(1, listofdetectorMappingsreturned.size());
        assertEquals(UUID.fromString(detectorUuid), listofdetectorMappingsreturned.get(0).getDetector().getUuid());
        assertEquals(id, listofdetectorMappingsreturned.get(0).getId());
    }

    @Test(expected = RuntimeException.class)
    public void testGetLastUpdated_fail() throws IOException {
        val TimeinSecs = 60;
        when(detectorMappingService.findLastUpdated(TimeinSecs)).thenThrow(new IOException());
        List<DetectorMapping> listofdetectorMappingsreturned = controllerUnderTest.findDetectorMapping(TimeinSecs);
        assertNotNull("Response can't be null", listofdetectorMappingsreturned);
        assertEquals(0, listofdetectorMappingsreturned.size());
    }

    @Test
    public void testDetectorMappingsearch() throws Exception {
        List<DetectorMapping> detectorMappingslist = mockDetectorMappingsList();
        SearchMappingsRequest searchMappingsRequest = new SearchMappingsRequest();
        searchMappingsRequest.setDetectorUuid(UUID.fromString(detectorUuid));
        searchMappingsRequest.setUserId(userVal);
        when(detectorMappingService.search(searchMappingsRequest)).thenReturn(detectorMappingslist);
        List<DetectorMapping> detectorMappingsResponse = controllerUnderTest.searchDetectorMapping(searchMappingsRequest);
        assertEquals(id, detectorMappingsResponse.get(0).getId());
        assertEquals(detectorUuid, detectorMappingsResponse.get(0).getDetector().getUuid().toString());
        assertEquals("test-user", detectorMappingsResponse.get(0).getUser().getId());
    }

    @Test
    public void testFindMatchingByTags() throws Exception {
        val lookuptime = 60;
        List<Map<String, String>> tagsList = new ArrayList<>();
        MatchingDetectorsResponse mockmatchingDetectorsResponse = mockMatchingDetectorsResponse(lookuptime, detectorUuid);
        when(detectorMappingService.findMatchingDetectorMappings(tagsList)).thenReturn(mockmatchingDetectorsResponse);
        MatchingDetectorsResponse matchingDetectorsResult = controllerUnderTest.searchDetectorMapping(tagsList);
        Assert.assertEquals(1, matchingDetectorsResult.getGroupedDetectorsBySearchIndex().size());
        List<Detector> detectors = matchingDetectorsResult.getGroupedDetectorsBySearchIndex().get(1);
        assertEquals(1, detectors.size());
        assertEquals(UUID.fromString(detectorUuid), detectors.get(0).getUuid());
    }

    private DetectorMapping mockDetectorMapping(String id) {
        DetectorMapping detectorMapping = mock(DetectorMapping.class);
        Detector detector = new Detector(UUID.fromString(detectorUuid));
        User user = new User(userVal);
        when(detectorMapping.getDetector()).thenReturn(detector);
        when(detectorMapping.getId()).thenReturn(id);
        when(detectorMapping.getUser()).thenReturn(user);
        when(detectorMapping.isEnabled()).thenReturn(true);
        return detectorMapping;
    }

    private List<DetectorMapping> mockDetectorMappingsList() {
        DetectorMapping detectorMapping = mock(DetectorMapping.class);
        List<DetectorMapping> detectorMappingsList = new ArrayList<>();
        Detector detector = new Detector(UUID.fromString(detectorUuid));
        when(detectorMapping.getDetector()).thenReturn(detector);
        when(detectorMapping.getId()).thenReturn(id);
        when(detectorMapping.getUser()).thenReturn(new User(userVal));
        detectorMappingsList.add(detectorMapping);
        return detectorMappingsList;
    }

    private MatchingDetectorsResponse mockMatchingDetectorsResponse(int lookuptime, String detectorUuid) {
        Map<Integer, List<Detector>> matchingDetectorsResponseMap = new HashMap<>();
        List<Detector> DetectorList = new ArrayList<>();
        DetectorList.add(new Detector(UUID.fromString(detectorUuid)));
        matchingDetectorsResponseMap.put(1, DetectorList);
        MatchingDetectorsResponse matchingDetectorsResponse = new MatchingDetectorsResponse(matchingDetectorsResponseMap, lookuptime);
        return matchingDetectorsResponse;
    }
}
