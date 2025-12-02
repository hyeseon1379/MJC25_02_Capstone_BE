package ac.kr.mjc.capstone.domain.packaze.service;

import ac.kr.mjc.capstone.domain.packaze.entity.Packaze;
import ac.kr.mjc.capstone.domain.packaze.repository.PackazeRepository;
import ac.kr.mjc.capstone.global.error.CustomException;
import ac.kr.mjc.capstone.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PackazeService {
    private final PackazeRepository packageRepository;

    @Transactional(readOnly = true)
    public List<Packaze> getAllActivePackages() {
        return packageRepository.findAllByIsActiveTrue();
    }

    @Transactional(readOnly = true)
    public Packaze getPackageById(Long id) {
        return packageRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND,"패키지를 찾을 수 없습니다22."));
    }
}