package kopo.springjwt.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.jpamongoprj.dto.NoticeDTO;
import poly.jpamongoprj.repository.NoticeRepository;
import poly.jpamongoprj.repository.entity.NoticeEntity;
import poly.jpamongoprj.service.INoticeService;
import poly.jpamongoprj.util.CmmUtil;
import poly.jpamongoprj.util.DateUtil;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service("NoticeService")
public class NoticeService implements INoticeService {

    private final NoticeRepository noticeRepository;

    @Override
    public List<NoticeDTO> getNoticeList() {
        List<NoticeEntity> rList = noticeRepository.findAllByOrderByNoticeSeqDesc();

        List<NoticeDTO> nList = new ObjectMapper().convertValue(rList, new TypeReference<List<NoticeDTO>>() {
        });
        return nList;
    }

    @Transactional
    @Override
    public NoticeDTO getNoticeInfo(NoticeDTO pDTO, boolean type) throws Exception {
        NoticeEntity rEntity = noticeRepository.findByNoticeSeq(pDTO.getNoticeSeq());

        if (type){
            NoticeEntity pEntity = NoticeEntity.builder()
                    .noticeSeq(rEntity.getNoticeSeq()).noticeYn(rEntity.getNoticeYn())
                    .title(rEntity.getTitle()).contents(rEntity.getContents()).regId(rEntity.getRegId())
                    .userId(rEntity.getUserId()).regDt(rEntity.getRegDt()).chgId(rEntity.getChgId())
                    .chgDt(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss")).readCnt(rEntity.getReadCnt()+1).build();
            noticeRepository.save(pEntity);

            rEntity = noticeRepository.findByNoticeSeq(pDTO.getNoticeSeq());
        }
        NoticeDTO rDTO = new ObjectMapper().convertValue(rEntity, NoticeDTO.class);

        return rDTO;
    }

    @Transactional
    @Override
    public void updateNoticeInfo(NoticeDTO pDTO) throws Exception {
        String noticeSeq = CmmUtil.nvl(pDTO.getNoticeSeq());
        String title = CmmUtil.nvl(pDTO.getTitle());
        String noticeYn = CmmUtil.nvl(pDTO.getNoticeYn());
        String contents = CmmUtil.nvl(pDTO.getContents());
        String userId = CmmUtil.nvl(pDTO.getUserId());

        NoticeEntity rEntity = noticeRepository.findByNoticeSeq(pDTO.getNoticeSeq());

        NoticeEntity pEntity = NoticeEntity.builder()
                .noticeSeq(noticeSeq).title(title).noticeYn(noticeYn).contents(contents)
                .userId(userId)
                .readCnt(rEntity.getReadCnt()).regId(rEntity.getRegId()).regDt(rEntity.getRegDt())
                .chgId(userId).chgDt(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss")).build();

        noticeRepository.save(pEntity);

    }

    @Transactional
    @Override
    public void deleteNoticeInfo(NoticeDTO pDTO)throws Exception{
        String noticeSeq = CmmUtil.nvl(pDTO.getNoticeSeq());

        noticeRepository.deleteById(noticeSeq);
    }

    @Transactional
    @Override
    public void insertNoticeInfo(NoticeDTO pDTO) throws Exception {
        String title = CmmUtil.nvl(pDTO.getTitle());
        String noticeYn = CmmUtil.nvl(pDTO.getNoticeYn());
        String contents = CmmUtil.nvl(pDTO.getContents());
        String userId = CmmUtil.nvl(pDTO.getUserId());

        NoticeEntity pEntity = NoticeEntity.builder()
                .title(title).noticeYn(noticeYn).contents(contents).userId(userId).readCnt(0L)
                .regId(userId).regDt(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"))
                .chgId(userId).regDt(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss")).build();

        noticeRepository.save(pEntity);
    }
}
