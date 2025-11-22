package ac.kr.mjc.capstone.global.media.converter;

import ac.kr.mjc.capstone.global.media.entity.ImageUsageType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ImageUsageTypeConverter implements Converter<String, ImageUsageType> {

    @Override
    public ImageUsageType convert(String source) {
        return ImageUsageType.fromString(source);
    }
}
