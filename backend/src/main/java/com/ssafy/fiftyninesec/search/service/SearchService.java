package com.ssafy.fiftyninesec.search.service;

import com.ssafy.fiftyninesec.search.dto.EventRoomSearchRequestDto;
import com.ssafy.fiftyninesec.search.dto.EventRoomSearchResponseDto;
import com.ssafy.fiftyninesec.search.entity.EventRoomSearch;
import com.ssafy.fiftyninesec.search.repository.EventRoomSearchRepository;
import com.ssafy.fiftyninesec.solution.entity.EventRoom;
import com.ssafy.fiftyninesec.solution.entity.Member;
import com.ssafy.fiftyninesec.solution.repository.EventRoomRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class SearchService {

    private final EventRoomSearchRepository eventRoomSearchRepository; // Elasticsearch 레포지토리
    private final EventRoomRepository eventRoomRepository; // JPA 레포지토리
    private final ElasticsearchOperations elasticsearchOperations;

    // 애플리케이션 시작 시 데이터 동기화
    @PostConstruct
    public void synchronizeData() {
        // 기존 인덱스 삭제
        eventRoomSearchRepository.deleteAll();

        List<EventRoom> mysqlRooms = eventRoomRepository.findAll();
        List<EventRoomSearch> esRooms = mysqlRooms.stream()
                .map(this::convertToES)
                .collect(Collectors.toList());

        esRooms.forEach(room -> room.setTitleCompletion(room.getTitle())); // titleCompletion 설정
        eventRoomSearchRepository.saveAll(esRooms);

        log.info("------ Elasticsearch Synchronization ------");
    }

    public EventRoomSearch convertToES(EventRoom mysqlRoom) {
        Long memberId = Optional.ofNullable(mysqlRoom.getMember())
                .map(Member::getId)
                .orElseThrow(() -> new RuntimeException("Member is null for EventRoom ID: " + mysqlRoom.getId()));


        EventRoomSearch esRoom = new EventRoomSearch();
        esRoom.setRoomId(mysqlRoom.getId());
        esRoom.setMemberId(memberId);
        esRoom.setTitle(mysqlRoom.getTitle());
        esRoom.setDescription(mysqlRoom.getDescription());
        esRoom.setStatus(mysqlRoom.getStatus().name());
        esRoom.setCreatedAt(mysqlRoom.getCreatedAt());
        esRoom.setStartTime(mysqlRoom.getStartTime());
        esRoom.setEndTime(mysqlRoom.getEndTime());
        esRoom.setWinnerNum(mysqlRoom.getWinnerNum());
        esRoom.setEnterCode(mysqlRoom.getEnterCode());
        esRoom.setUnlockCount(mysqlRoom.getUnlockCount());
        esRoom.setBannerImage(mysqlRoom.getBannerImage());
        esRoom.setSquareImage(mysqlRoom.getSquareImage());
        esRoom.setRectangleImage(mysqlRoom.getRectangleImage());
        return esRoom;
    }

    public List<EventRoomSearchResponseDto> searchEventRooms(EventRoomSearchRequestDto requestDto, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<EventRoomSearch> eventRooms = eventRoomSearchRepository.findByTitle(requestDto.getKeyword(), pageable);
        return eventRooms.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    private EventRoomSearchResponseDto mapToResponseDto(EventRoomSearch eventRoomSearch) {
        return EventRoomSearchResponseDto.builder()
                .eventId(eventRoomSearch.getRoomId())
                .title(eventRoomSearch.getTitle())
                .description(eventRoomSearch.getDescription())
                .status(eventRoomSearch.getStatus())
                .createdAt(eventRoomSearch.getCreatedAt())
                .startTime(eventRoomSearch.getStartTime())
                .endTime(eventRoomSearch.getEndTime())
                .winnerNum(eventRoomSearch.getWinnerNum())
                .bannerImage(eventRoomSearch.getBannerImage())
                .squareImage(eventRoomSearch.getSquareImage())
                .rectangleImage(eventRoomSearch.getRectangleImage())
                .build();
    }

    public List<String> autocomplete(String keyword, int page, int size) {
        // 검색 키워드에서 공백 제거
        String processedKeyword = keyword.replace(" ", "");

        // CriteriaQuery를 이용해 title 필드에 keyword 포함 여부를 확인하고 페이지네이션 추가
        Criteria criteria = new Criteria("title").contains(processedKeyword);
        Pageable pageable = PageRequest.of(page, size);
        CriteriaQuery query = new CriteriaQuery(criteria, pageable);

        SearchHits<EventRoomSearch> searchHits = elasticsearchOperations.search(query, EventRoomSearch.class);

        return searchHits.stream()
                .map(hit -> hit.getContent().getTitle())
                .collect(Collectors.toList());
    }
}