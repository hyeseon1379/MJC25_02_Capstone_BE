package ac.kr.mjc.capstone.domain.packaze.controller;

import ac.kr.mjc.capstone.domain.packaze.entity.Packaze;
import ac.kr.mjc.capstone.domain.packaze.service.PackazeService;
import ac.kr.mjc.capstone.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/packages")
@RequiredArgsConstructor
public class PackazeController {

    private final PackazeService packageService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Packaze>>> getAllPackages() {
        List<Packaze> packages = packageService.getAllActivePackages();
        return ResponseEntity.ok(ApiResponse.success(packages));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Packaze>> getPackage(@PathVariable Long id) {
        Packaze packageItem = packageService.getPackageById(id);
        return ResponseEntity.ok(ApiResponse.success(packageItem));
    }
}
