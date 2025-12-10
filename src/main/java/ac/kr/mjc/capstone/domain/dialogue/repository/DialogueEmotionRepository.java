package ac.kr.mjc.capstone.domain.dialogue.repository;

import ac.kr.mjc.capstone.domain.dialogue.entity.DialogueEmotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DialogueEmotionRepository extends JpaRepository<DialogueEmotion, Long> {

    List<DialogueEmotion> findByConversationConversationId(Long conversationId);

    void deleteByConversationConversationId(Long conversationId);
}
