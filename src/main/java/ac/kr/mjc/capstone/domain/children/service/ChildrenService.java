package ac.kr.mjc.capstone.domain.children.service;


import ac.kr.mjc.capstone.domain.children.dto.ChildrenRequest;
import ac.kr.mjc.capstone.domain.children.dto.ChildrenResponse;
import ac.kr.mjc.capstone.domain.children.dto.ChildrenUpdateRequest;
import ac.kr.mjc.capstone.domain.children.entity.ChildrenEntity;
import ac.kr.mjc.capstone.domain.children.repository.ChildrenRepository;
import ac.kr.mjc.capstone.domain.user.entity.UserEntity;
import ac.kr.mjc.capstone.domain.user.repository.UserRepository;
import ac.kr.mjc.capstone.global.error.CustomException;
import ac.kr.mjc.capstone.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChildrenService {

    private final ChildrenRepository childrenRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChildrenResponse insert(ChildrenRequest request, Long userId) {
        // 사용자 조회
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 자녀 엔티티 생성
        ChildrenEntity childrenEntity = ChildrenEntity.builder()
                .childName(request.getChildName())
                .childBirth(request.getChildBirth())
                .birthOrder(request.getBirthOrder())
                .color(request.getColor())
                .profileImg(request.getProfileImg())
                .gender(request.getGender())
                .userEntity(userEntity)
                .build();

        ChildrenEntity savedChild = childrenRepository.save(childrenEntity);
        log.info("Child created: childId={}, childName={}, userId={}", 
                savedChild.getChildId(), savedChild.getChildName(), userId);
        
        return ChildrenResponse.from(savedChild);
    }

    @Transactional(readOnly = true)
    public List<ChildrenResponse> findAll(Long userId) {
        // 사용자 존재 확인
        if (!userRepository.existsById(userId)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        List<ChildrenEntity> children = childrenRepository.findAllByUserEntity_UserId(userId);
        
        return children.stream()
                .map(ChildrenResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ChildrenResponse findOne(Long childId, Long userId) {
        ChildrenEntity childrenEntity = childrenRepository.findById(childId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        
        // 본인의 자녀인지 확인 (보안)
        if (!childrenEntity.getUserEntity().getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        return ChildrenResponse.from(childrenEntity);
    }

    @Transactional
    public ChildrenResponse update(Long childId, ChildrenUpdateRequest request, Long userId) {
        // 자녀 조회
        ChildrenEntity childrenEntity = childrenRepository.findById(childId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        
        // 본인의 자녀인지 확인 (보안)
        if (!childrenEntity.getUserEntity().getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        // 변경 감지(Dirty Checking)로 업데이트
        childrenEntity.updateProfile(
                request.getChildName(),
                request.getChildBirth(),
                request.getGender(),
                request.getBirthOrder(),
                request.getColor(),
                request.getProfileImg()
        );

        log.info("Child updated: childId={}, childName={}, userId={}", 
                childId, childrenEntity.getChildName(), userId);
        
        return ChildrenResponse.from(childrenEntity);
    }

    @Transactional
    public void delete(Long childId, Long userId) {
        // 자녀 조회
        ChildrenEntity childrenEntity = childrenRepository.findById(childId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        
        // 본인의 자녀인지 확인 (보안)
        if (!childrenEntity.getUserEntity().getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        
        // 삭제
        childrenRepository.deleteById(childId);
        
        log.info("Child deleted: childId={}, childName={}, userId={}", 
                childId, childrenEntity.getChildName(), userId);
    }

}
