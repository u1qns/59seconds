package com.ssafy.fiftyninesec.search.repository;

import com.ssafy.fiftyninesec.search.entity.EventRoomSearch;
import com.ssafy.fiftyninesec.solution.entity.EventRoom;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRoomSearchRepository extends ElasticsearchRepository<EventRoomSearch, Integer> {

    List<EventRoomSearch> findByTitleContaining(String keyword);
}

