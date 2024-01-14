package me.hamtom.thor.directory.domain.info;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.hamtom.thor.directory.domain.common.DirectoryService;
import me.hamtom.thor.directory.domain.common.entity.Directory;
import me.hamtom.thor.directory.domain.common.exception.PredictableRuntimeException;
import me.hamtom.thor.directory.domain.info.dto.GetInfoCommand;
import me.hamtom.thor.directory.domain.info.dto.GetInfoResult;
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
    public GetInfoResult getDirectoryInfo(GetInfoCommand command) {
        String pathName = command.getPathName();

        ifRootPath(pathName);

        //검색 디렉토리 존재 확인, 가져오기. 존재 X -> 실패 응답
        Directory directory = directoryService.checkExistAndGetDirectory(pathName);

        String formattedCreated = localDateTimeToStr(directory.getCreated());
        String formattedModified = localDateTimeToStr(directory.getModified());
        return new GetInfoResult(
                directory.getPathName(),
                directory.getOwner(),
                directory.getGroup(),
                directory.getPermissions(),
                directory.getSize(),
                formattedCreated,
                formattedModified
        );
    }

    private void ifRootPath(String pathName) {
        boolean isRootPath = directoryService.isRootPath(pathName);
        if (isRootPath) {
            throw new PredictableRuntimeException("root 경로(/)입니다. 조회할 수 없습니다.");
        }
    }

    private String localDateTimeToStr(LocalDateTime localDateTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return localDateTime.format(dateTimeFormatter);
    }

}
