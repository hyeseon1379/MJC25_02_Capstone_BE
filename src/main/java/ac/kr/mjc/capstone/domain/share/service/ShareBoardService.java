package ac.kr.mjc.capstone.domain.share.service;

import ac.kr.mjc.capstone.domain.share.dto.ShareBoardRequest;
import ac.kr.mjc.capstone.domain.share.dto.ShareBoardResponse;
import ac.kr.mjc.capstone.domain.share.dto.ShareStatusUpdateRequest;
import ac.kr.mjc.capstone.domain.share.entity.MeetStatus;
import ac.kr.mjc.capstone.domain.share.entity.ShareBoardEntity;
import ac.kr.mjc.capstone.domain.share.repository.ShareBoardRepository;
import ac.kr.mjc.capstone.domain.user.entity.UserEntity;
import ac.kr.mjc.capstone.domain.user.repository.UserRepository;
import ac.kr.mjc.capstone.global.error.CustomException;
import ac.kr.mjc.capstone.global.error.ErrorCode;
import ac.kr.mjc.capstone.global.media.dto.ImageFileResponse;
import ac.kr.mjc.capstone.global.media.entity.ImageFileEntity;
import ac.kr.mjc.capstone.global.media.entity.ImageUsageType;
import ac.kr.mjc.capstone.global.media.repository.FileRepository;
import ac.kr.mjc.capstone.global.media.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShareBoardService {

    private final ShareBoardRepository shareBoardRepository;
    private final UserRepository userRepository;
    private final FileRepository fileRepository;
    private final FileService fileService;

    @Transactional(readOnly = true)
    public Page<ShareBoardResponse> getShareBoardList(String keyword, MeetStatus status, Pageable pageable) {
        Page<ShareBoardEntity> boards = shareBoardRepository.findByKeywordAndStatus(keyword, status, pageable);
        log.info("ShareBoard list retrieved: totalElements={}, keyword={}, status={}",
                boards.getTotalElements(), keyword, status);
        return boards.map(ShareBoardResponse::fromList);
    }

    @Transactional
    public ShareBoardResponse getShareBoard(Long shareId) {
        ShareBoardEntity board = shareBoardRepository.findById(shareId)
                .orElseThrow(() -> new CustomException(ErrorCode.SHARE_NOT_FOUND));
        board.incrementViews();
        log.info("ShareBoard retrieved: shareId={}, title={}, views={}",
                shareId, board.getTitle(), board.getViews());
        return ShareBoardResponse.fromDetail(board);
    }

    @Transactional
    public Long createShareBoard(ShareBoardRequest request, MultipartFile image, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        ImageFileEntity imageEntity = null;
        if (image != null && !image.isEmpty()) {
            ImageFileResponse imageResponse = fileService.uploadImage(image, ImageUsageType.SHARE);
            imageEntity = fileRepository.findById(imageResponse.getImageId()).orElse(null);
        }

        MeetStatus status = request.getMeetStatus() != null ? request.getMeetStatus() : MeetStatus.SHARING;

        ShareBoardEntity board = ShareBoardEntity.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .user(user)
                .imageFile(imageEntity)
                .meetStatus(status)
                .build();

        ShareBoardEntity savedBoard = shareBoardRepository.save(board);
        log.info("ShareBoard created: shareId={}, title={}, userId={}",
                savedBoard.getShareId(), savedBoard.getTitle(), userId);
        return savedBoard.getShareId();
    }

    @Transactional
    public Long updateShareBoard(Long shareId, ShareBoardRequest request, MultipartFile image, Long userId) {
        ShareBoardEntity board = shareBoardRepository.findById(shareId)
                .orElseThrow(() -> new CustomException(ErrorCode.SHARE_NOT_FOUND));

        if (!board.getUser().getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        ImageFileEntity imageEntity = board.getImageFile();
        if (image != null && !image.isEmpty()) {
            if (imageEntity != null) {
                fileService.deleteImage(imageEntity.getImageId());
            }
            ImageFileResponse imageResponse = fileService.uploadImage(image, ImageUsageType.SHARE);
            imageEntity = fileRepository.findById(imageResponse.getImageId()).orElse(null);
        }

        board.updateShareBoard(request.getTitle(), request.getContent(), request.getMeetStatus(), imageEntity);
        log.info("ShareBoard updated: shareId={}, title={}, userId={}", shareId, board.getTitle(), userId);
        return board.getShareId();
    }

    @Transactional
    public void deleteShareBoard(Long shareId, Long userId) {
        ShareBoardEntity board = shareBoardRepository.findById(shareId)
                .orElseThrow(() -> new CustomException(ErrorCode.SHARE_NOT_FOUND));

        if (!board.getUser().getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        if (board.getImageFile() != null) {
            fileService.deleteImage(board.getImageFile().getImageId());
        }

        shareBoardRepository.delete(board);
        log.info("ShareBoard deleted: shareId={}, userId={}", shareId, userId);
    }

    @Transactional
    public ShareBoardResponse updateStatus(Long shareId, ShareStatusUpdateRequest request, Long userId) {
        ShareBoardEntity board = shareBoardRepository.findById(shareId)
                .orElseThrow(() -> new CustomException(ErrorCode.SHARE_NOT_FOUND));

        if (!board.getUser().getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        board.updateStatus(request.getMeetStatus());
        log.info("ShareBoard status updated: shareId={}, newStatus={}, userId={}",
                shareId, request.getMeetStatus(), userId);

        return ShareBoardResponse.builder()
                .shareId(board.getShareId())
                .meetStatus(board.getMeetStatus())
                .build();
    }
}
