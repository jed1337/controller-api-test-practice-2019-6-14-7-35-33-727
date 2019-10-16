package com.tw.api.unit.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tw.api.unit.test.domain.todo.Todo;
import com.tw.api.unit.test.domain.todo.TodoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TodoController.class)
@ActiveProfiles(profiles = "test")
class TodoControllerTest {
    @Autowired
    private TodoController todoController;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TodoRepository todoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getTodo() throws Exception {
        //given
        when(todoRepository.findById(5)).thenReturn(Optional.of(new Todo(5, "todo title", false, 5)));
        //when
        ResultActions result = mvc.perform(get("/todos/5"));
        //then
        result.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.title", is("todo title")))
                .andExpect(jsonPath("$.completed", is(false)))
                .andExpect(jsonPath("$.order", is(5)))
        ;
    }

    @Test
    void getAll() throws Exception {
        //given
        List<Todo> todoList = new ArrayList<>();
        todoList.add(new Todo(1, "don't get zucced", false, 1));
        todoList.add(new Todo(2, "get out of facebook jail", false, 2));

        when(todoRepository.getAll()).thenReturn(todoList);
        //when
        ResultActions result = mvc.perform(get("/todos"));
        //then
        result.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(2)))

                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("don't get zucced")))
                .andExpect(jsonPath("$[0].completed", is(false)))
                .andExpect(jsonPath("$[0].order", is(1)))

                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].title", is("get out of facebook jail")))
                .andExpect(jsonPath("$[1].completed", is(false)))
                .andExpect(jsonPath("$[1].order", is(2)))
        ;
    }

    @Test
    void saveTodo() throws Exception {
        //given
        Todo todo = new Todo("new todo", false);

        //when
        ResultActions result = mvc.perform(post("/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todo))
        );

        //then
        result.andExpect(status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("new todo")))
        ;
    }

    @Test
    public void should_delete_one_existing_todo() throws Exception {
        when(todoRepository.findById(11)).thenReturn(Optional.of(new Todo(11, "eleven", false, 11)));

        ResultActions result = mvc.perform(delete("/todos/11"));

        result.andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void should_not_delete_non_existing_todo() throws Exception {
        when(todoRepository.findById(22)).thenReturn(Optional.empty());

        ResultActions result = mvc.perform(delete("/todos/22"));

        result.andExpect(status().isNotFound())
                .andDo(print());
    }
}
