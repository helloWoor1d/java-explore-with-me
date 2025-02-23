package ru.practicum.stats.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.stats.mapper.HitMapper;
import ru.practicum.stats.mapper.HitMapperImpl;
import ru.practicum.stats.model.EndpointHitDto;
import ru.practicum.stats.model.ViewStats;
import ru.practicum.stats.service.StatsService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import(HitMapperImpl.class)
public class StatsControllerTest {
    private final MockMvc mvc;
    private final ObjectMapper objectMapper;
    private static HitMapper hitMapper;

    @InjectMocks
    private StatsController controller;

    @MockBean
    private StatsService statsService;

    public StatsControllerTest(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper,
                               @Autowired StatsController statsController) {
        this.mvc = mvc;
        this.objectMapper = objectMapper;
        this.controller = statsController;
    }

    @Test
    public void shouldHit() throws Exception {
        doNothing().when(statsService).saveHit(any());
        EndpointHitDto hitDto = EndpointHitDto.builder()
                .app("ewm-main-service")
                .uri("/events/1")
                .ip("192.168.0.1")
                .timestamp("2022-09-06 11:00:23")
                .build();
        mvc.perform(post("/hit")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(hitDto)))
                .andExpect(status().isCreated());
        verify(statsService, Mockito.times(1)).saveHit(any());
    }

    @Test
    public void shouldGetStats() throws Exception {
        ViewStats stats = ViewStats.builder()
                .app("ewm-main-service")
                .uri("/events/1")
                .hits(1L)
                .build();
        when(statsService.getStats(any(), any(), anyList(), anyBoolean())).thenReturn(List.of(stats));

        MockHttpServletResponse response = performGetStats("2020-05-05 00:00:00", "2035-05-05 00:00:00",
                List.of(stats), true);
        assertThat(response.getStatus(), is(200));
        assertThat(response.getContentAsString(), is(objectMapper.writeValueAsString(List.of(stats))));

        response = performGetStats("2020-05-05 00:00:00", "2035-05-05 00:00:00");
        assertThat(response.getStatus(), is(200));
    }

    public MockHttpServletResponse performGetStats(String start, String end,
                                                   List<ViewStats> uris, Boolean unique) throws Exception {
        MvcResult result = mvc.perform(get("/stats")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("start", start)
                        .param("end", end)
                        .param("uris", objectMapper.writeValueAsString(uris))
                        .param("unique", String.valueOf(unique)))
                .andReturn();
        return result.getResponse();
    }

    public MockHttpServletResponse performGetStats(String start, String end) throws Exception {
        MvcResult result = mvc.perform(get("/stats")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("start", start)
                        .param("end", end))
                .andReturn();
        return result.getResponse();
    }
}

