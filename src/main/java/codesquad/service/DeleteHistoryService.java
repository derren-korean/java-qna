package codesquad.service;

import codesquad.domain.ContentType;
import codesquad.domain.DeleteHistory;
import codesquad.domain.DeleteHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service("deleteHistoryService")
public class DeleteHistoryService {
    @Resource(name = "deleteHistoryRepository")
    private DeleteHistoryRepository deleteHistoryRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAll(List<DeleteHistory> deleteHistories) {
        for (DeleteHistory deleteHistory : deleteHistories) {
            deleteHistoryRepository.save(deleteHistory);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(DeleteHistory deleteHistory) {
        deleteHistoryRepository.save(deleteHistory);
    }

    public DeleteHistory findByContentIdAndContentType(long contentId, ContentType type) {
        return deleteHistoryRepository.findByContentIdAndContentType(contentId, type);
    }
}
