package kopo.springjwt.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import poly.jpamongoprj.repository.entity.NoticeEntity;

import java.util.List;

@Repository
public interface NoticeRepository extends MongoRepository<NoticeEntity, String> {

    List<NoticeEntity> findAllByOrderByNoticeSeqDesc();

    NoticeEntity findByNoticeSeq(String noticeSeq);
}
