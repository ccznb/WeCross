package com.webank.wecross.test.restserver;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.webank.wecross.account.Accounts;
import com.webank.wecross.host.WeCrossHost;
import com.webank.wecross.resource.Path;
import com.webank.wecross.resource.Resource;
import com.webank.wecross.resource.TestResource;
import com.webank.wecross.restserver.RestfulController;
import com.webank.wecross.restserver.response.GetDataResponse;
import com.webank.wecross.restserver.response.ResourceResponse;
import com.webank.wecross.restserver.response.SetDataResponse;
import com.webank.wecross.restserver.response.TransactionResponse;
import com.webank.wecross.zone.ZoneManager;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

// To run with: gradle test --tests RestfulServiceTest

@RunWith(SpringRunner.class)
// @SpringBootTest
@WebMvcTest(RestfulController.class)
// @AutoConfigureMockMvc
// @TestExecutionListeners( { DependencyInjectionTestExecutionListener.class })
// @ContextConfiguration(classes=RestfulServiceTestConfig.class)
public class RestfulControllerTest {
    @Autowired private MockMvc mockMvc;

    @MockBean(name = "newWeCrossHost")
    private WeCrossHost weCrossHost;

    @MockBean(name = "newAccounts")
    private Accounts accounts;

    @Test
    public void okTest() throws Exception {
        try {
            MvcResult rsp =
                    this.mockMvc
                            .perform(get("/test"))
                            .andDo(print())
                            .andExpect(status().isOk())
                            .andReturn();

            String result = rsp.getResponse().getContentAsString();
            System.out.println("####Respond: " + result);

            String expectRsp = "OK!";
            Assert.assertEquals(expectRsp, result);
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void statusTest() throws Exception {
        try {
            Mockito.when(weCrossHost.getResource(Mockito.any())).thenReturn(new TestResource());

            MvcResult rsp =
                    this.mockMvc
                            .perform(get("/test-network/test-stub/test-resource/status"))
                            .andDo(print())
                            .andExpect(status().isOk())
                            .andReturn();

            String result = rsp.getResponse().getContentAsString();
            System.out.println("####Respond: " + result);

            String expectRsp =
                    "{\"version\":\"1\",\"result\":0,\"message\":\"Success\",\"data\":\"exists\"}";
            Assert.assertEquals(expectRsp, result);
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void listTest() throws Exception {
        try {
            ResourceResponse resourceResponse = new ResourceResponse();
            resourceResponse.setErrorCode(0);
            resourceResponse.setErrorMessage("");
            resourceResponse.setResources(new ArrayList<Resource>());

            ZoneManager mockNetworkManager = Mockito.mock(ZoneManager.class);
            Mockito.when(mockNetworkManager.list(Mockito.any())).thenReturn(resourceResponse);

            Mockito.when(weCrossHost.getZoneManager()).thenReturn(mockNetworkManager);

            String json =
                    "{\n"
                            + "\"version\":\"1\",\n"
                            + "\"path\":\"\",\n"
                            + "\"method\":\"list\",\n"
                            + "\"data\": {\n"
                            + "\"ignoreRemote\":true\n"
                            + "}\n"
                            + "}";

            MvcResult rsp =
                    this.mockMvc
                            .perform(
                                    post("/list")
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(json))
                            .andDo(print())
                            .andExpect(status().isOk())
                            .andReturn();

            String result = rsp.getResponse().getContentAsString();
            System.out.println("####Respond: " + result);

            String expectRsp =
                    "{\"version\":\"1\",\"result\":0,\"message\":\"Success\",\"data\":{\"errorCode\":0,\"errorMessage\":\"\",\"resources\"";
            Assert.assertTrue(result.contains(expectRsp));
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void setDataTest() throws Exception {
        try {
            SetDataResponse setDataResponse = new SetDataResponse();
            setDataResponse.setErrorCode(0);
            setDataResponse.setErrorMessage("setData test resource success");

            Resource resource = Mockito.mock(Resource.class);
            Mockito.when(resource.setData(Mockito.any())).thenReturn(setDataResponse);

            Mockito.when(weCrossHost.getResource(Mockito.isA(Path.class))).thenReturn(resource);

            String json =
                    "{\n"
                            + "\"version\":\"1\",\n"
                            + "\"path\":\"test-network.test-stub.test-resource\",\n"
                            + "\"method\":\"setData\",\n"
                            + "\"data\": {\n"
                            + "\"key\":\"mockKey\",\n"
                            + "\"value\":\"mockValueaaaa\"\n"
                            + "}\n"
                            + "}";

            MvcResult rsp =
                    this.mockMvc
                            .perform(
                                    post("/test-network/test-stub/test-resource/setData")
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(json))
                            .andDo(print())
                            .andExpect(status().isOk())
                            .andReturn();

            String result = rsp.getResponse().getContentAsString();
            System.out.println("####Respond: " + result);

            String expectRsp =
                    "{\"version\":\"1\",\"result\":0,\"message\":\"Success\",\"data\":{\"errorCode\":0,\"errorMessage\":\"setData test resource success\"}}";
            Assert.assertTrue(result.contains(expectRsp));
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void getDataTest() throws Exception {
        try {
            GetDataResponse getDataResponse = new GetDataResponse();
            getDataResponse.setErrorCode(0);
            getDataResponse.setErrorMessage("getData test resource success");

            Resource resource = Mockito.mock(Resource.class);
            Mockito.when(resource.getData(Mockito.any())).thenReturn(getDataResponse);

            Mockito.when(weCrossHost.getResource(Mockito.isA(Path.class))).thenReturn(resource);

            String json =
                    "{\n"
                            + "\"version\":\"1\",\n"
                            + "\"path\":\"test-network.test-stub.test-resource\",\n"
                            + "\"method\":\"getData\",\n"
                            + "\"data\": {\n"
                            + "\"key\":\"mockKey\"\n"
                            + "}\n"
                            + "}";

            MvcResult rsp =
                    this.mockMvc
                            .perform(
                                    post("/test-network/test-stub/test-resource/getData")
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(json))
                            .andDo(print())
                            .andExpect(status().isOk())
                            .andReturn();

            String result = rsp.getResponse().getContentAsString();
            System.out.println("####Respond: " + result);

            String expectRsp =
                    "{\"version\":\"1\",\"result\":0,\"message\":\"Success\",\"data\":{\"errorCode\":0,\"errorMessage\":\"getData test resource success\"";
            Assert.assertTrue(result.contains(expectRsp));
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage(), false);
        }
    }

    public void callTest() throws Exception {
        try {
            TransactionResponse transactionResponse = new TransactionResponse();
            transactionResponse.setErrorCode(0);
            transactionResponse.setErrorMessage("call test resource success");
            transactionResponse.setHash("010157f4");

            Resource resource = Mockito.mock(Resource.class);
            Mockito.when(resource.call(Mockito.any())).thenReturn(transactionResponse);

            Mockito.when(weCrossHost.getResource(Mockito.isA(Path.class))).thenReturn(resource);

            String json =
                    "{\n"
                            + "\"version\":\"1\",\n"
                            + "\"path\":\"test-network.test-stub.test-resource\",\n"
                            + "\"method\":\"call\",\n"
                            + "\"data\": {\n"
                            + "\"sig\":\"\",\n"
                            + "\"method\":\"get\",\n"
                            + "\"args\":[]\n"
                            + "}\n"
                            + "}";

            MvcResult rsp =
                    this.mockMvc
                            .perform(
                                    post("/test-network/test-stub/test-resource/call")
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(json))
                            .andDo(print())
                            .andExpect(status().isOk())
                            .andReturn();

            String result = rsp.getResponse().getContentAsString();
            System.out.println("####Respond: " + result);

            String expectRsp =
                    "{\"version\":\"1\",\"result\":0,\"message\":\"Success\",\"data\":{\"errorCode\":0,\"errorMessage\":\"call test resource success\",\"hash\":\"010157f4\",";
            Assert.assertTrue(result.contains(expectRsp));
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage(), false);
        }
    }

    public void sendTransactionTest() throws Exception {
        try {
            TransactionResponse transactionResponse = new TransactionResponse();
            transactionResponse.setErrorCode(0);
            transactionResponse.setErrorMessage("sendTransaction test resource success");
            transactionResponse.setHash("010157f4");

            Resource resource = Mockito.mock(Resource.class);
            Mockito.when(resource.sendTransaction(Mockito.any())).thenReturn(transactionResponse);

            Mockito.when(weCrossHost.getResource(Mockito.isA(Path.class))).thenReturn(resource);

            String json =
                    "{\n"
                            + "\"version\":\"1\",\n"
                            + "\"path\":\"test-network.test-stub.test-resource\",\n"
                            + "\"method\":\"sendTransaction\",\n"
                            + "\"data\": {\n"
                            + "\"sig\":\"\",\n"
                            + "\"method\":\"set\",\n"
                            + "\"args\":[\"aaaaa\"]\n"
                            + "}\n"
                            + "}";

            MvcResult rsp =
                    this.mockMvc
                            .perform(
                                    post("/test-network/test-stub/test-resource/sendTransaction")
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(json))
                            .andDo(print())
                            .andExpect(status().isOk())
                            .andReturn();

            String result = rsp.getResponse().getContentAsString();
            System.out.println("####Respond: " + result);

            String expectRsp =
                    "{\"version\":\"1\",\"result\":0,\"message\":\"Success\",\"data\":{\"errorCode\":0,\"errorMessage\":\"sendTransaction test resource success\",\"hash\":\"010157f4\",";
            Assert.assertTrue(result.contains(expectRsp));
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void exceptionTest() {
        try {
            String json =
                    "{\n"
                            + "\"version\":\"1\",\n"
                            + "\"path\":\"test-network.test-stub.test-resource\",\n"
                            + "\"method\":\"sendTransaction\",\n"
                            + "\"data\": {\n"
                            + "\"sig\":\"\",\n"
                            + "\"method\":\"set\",\n"
                            + "\"args\":[\"aaaaa\"]\n"
                            + "}\n"
                            + "}";

            MvcResult rsp =
                    this.mockMvc
                            .perform(
                                    post("/test-network/test-stub/test-resource/notExistMethod")
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(json))
                            .andDo(print())
                            .andExpect(status().isOk())
                            .andReturn();

            String result = rsp.getResponse().getContentAsString();
            System.out.println("####Respond: " + result);

            String expectRsp =
                    "{\"version\":\"1\",\"result\":20001,\"message\":\"Unsupported method: notExistMethod\",\"data\":null}";
            Assert.assertTrue(result.contains(expectRsp));
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage(), false);
        }
    }

    @Before
    public void beforeTest() {
        // mockMvc = MockMvcBuilders.standaloneSetup(RestfulController.class).build();
        System.out.println("------------------------Test begin------------------------");
    }

    @After
    public void afterTest() {
        System.out.println("-------------------------Test end-------------------------");
    }

    @BeforeClass
    public static void beforeClassTest() {
        System.out.println("beforeClassTest");
    }

    @AfterClass
    public static void afterClassTest() {
        System.out.println("afterClassTest");
    }
}
