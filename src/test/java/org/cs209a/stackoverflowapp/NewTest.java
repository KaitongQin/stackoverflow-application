package org.cs209a.stackoverflowapp;
import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import org.cs209a.stackoverflowapp.entity.Question;
import org.cs209a.stackoverflowapp.mapper.QuestionMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisPlusTest
public class NewTest {
    @Autowired
    private QuestionMapper sampleMapper;

    @Test
    void testInsert() {
        Question sample = new Question();
        sampleMapper.insert(sample);
        assertThat(sample.getBody()).isNotNull();
    }
}
