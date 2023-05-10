package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exceptions.SearchException;
import ru.yandex.practicum.filmorate.utils.Edata;
import ru.yandex.practicum.filmorate.utils.Storage;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public abstract class StorageTest<T extends Edata> {
    protected final Storage<T> storage;
    protected T data1, data2, data3;

    abstract void initData();

    @Test
    void shouldAddData() {
        T newData = storage.add(data1);
        data1.setId(newData.getId());

        T actual = storage.findById(Long.valueOf(newData.getId()));
        assertThat(actual).isNotNull()
                .isEqualTo(data1);
    }

    @Test
    void shouldNotAddNull() {
        T actual = storage.add(null);
        assertThat(actual).isNull();
    }

    @Test
    void shouldUpdateData() {
        T newData = storage.add(data1);
        data2.setId(newData.getId());
        storage.update(data2);

        T actual = storage.findById(Long.valueOf(data2.getId()));
        assertThat(actual).isNotNull()
                .isEqualTo(data2);
    }

    @Test
    void shouldNotUpdateWithWrongId() {
        T newData = storage.add(data1);
        data1.setId(newData.getId());
        List<T> dataList = new ArrayList<>();
        dataList.add(data1);

        data2.setId(Math.toIntExact((long) -1));
        assertThatThrownBy(() -> storage.update(data2))
                .isInstanceOf(SearchException.class);

        List<T> actual = storage.findAll();
        assertThat(actual).hasSize(1)
                .isEqualTo(dataList);
    }

    @Test
    void shouldFindAll() {
        data1.setId(storage.add(data1).getId());
        data2.setId(storage.add(data2).getId());
        data3.setId(storage.add(data3).getId());

        List<T> dataList = new ArrayList<>();
        dataList.add(data1);
        dataList.add(data2);
        dataList.add(data3);
        List<T> actual = storage.findAll();
        assertThat(actual).isNotNull()
                .hasSize(3)
                .isEqualTo(dataList);
    }

    @Test
    void shouldFindById() {
        T newData = storage.add(data1);
        data1.setId(newData.getId());

        T actual = storage.findById(Long.valueOf(newData.getId()));
        assertThat(actual).isNotNull()
                .isEqualTo(data1);
    }

    @Test
    void shouldNotFindWithWrongId() {
        T actual = storage.findById((long) -1);
        assertThat(actual).isNull();
    }

    @Test
    void shouldReturnTrueIfContains() {
        T newData = storage.add(data1);
        data1.setId(newData.getId());

        boolean actual = storage.contains(Long.valueOf(newData.getId()));
        assertThat(actual).isTrue();
    }

    @Test
    void shouldReturnFalseIfNotContains() {
        boolean actual = storage.contains(-1L);
        assertThat(actual).isFalse();
    }
}
