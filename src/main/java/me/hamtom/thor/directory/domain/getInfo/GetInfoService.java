package me.hamtom.thor.directory.domain.getInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.hamtom.thor.directory.domain.common.DirectoryService;
import me.hamtom.thor.directory.domain.common.directory.entity.Directory;
import me.hamtom.thor.directory.domain.common.exception.PredictableRuntimeException;
import me.hamtom.thor.directory.domain.getInfo.dto.GetDirectoryInfoResultDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetInfoService {

    private final DirectoryService directoryService;

    @Transactional(readOnly = true)
    public GetDirectoryInfoResultDto getDirectoryInfo(String pathName) {
        //검색 디렉토리 존재 확인, 가져오기. 존재 X -> 실패 응답
        Directory directory = directoryService.checkExistAndGetDirectory(pathName);

        String formattedCreated = localDateTimeToStr(directory.getCreated());
        String formattedModified = localDateTimeToStr(directory.getModified());
        return new GetDirectoryInfoResultDto(
                directory.getPathName(),
                directory.getOwner(),
                directory.getGroup(),
                directory.getPermissions(),
                directory.getSize(),
                formattedCreated,
                formattedModified
        );
    }

    private String localDateTimeToStr(LocalDateTime localDateTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return localDateTime.format(dateTimeFormatter);
    }

}
