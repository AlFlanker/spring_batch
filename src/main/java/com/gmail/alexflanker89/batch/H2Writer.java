package com.gmail.alexflanker89.batch;


import com.gmail.alexflanker89.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.JdbcOperations;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
public class H2Writer implements ItemWriter<User> {
    private final JdbcOperations jdbcOperations;

    @Override
    public void write(List<? extends User> users) throws Exception {
        StringBuilder values = new StringBuilder(" VALUES ");
        for (int i = 0; i < users.size(); i++) {
            values.append(" (?, ?, ?, ?, ?)");
            if (i < users.size() - 1) values.append(", ");
        }

        List<Object> collect = users.stream()
                .map(user -> new Object[]{
                        user.getId(),
                        user.getUsername(),
                        user.getPassword(),
                        user.isActive(),
                        user.getRoles().iterator().next()
                })
                .flatMap(objects -> Arrays.asList(objects).stream())
                .collect(Collectors.toList());


        String sql = "INSERT INTO usr (id, username, password, active, role)" + values.toString();
        collect.forEach(o -> log.warn(o.toString()));
        jdbcOperations.update(sql,collect.toArray());
    }
}
