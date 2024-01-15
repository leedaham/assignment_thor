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
@Transactional(readOnly = true)
public class GetInfoService {

    private final DirectoryService directoryService;

    /**
     * 디렉토리 조회 메서드
     * @return 디렉토리 조회 결과
     */

    public GetInfoResult getDirectoryInfo(GetInfoCommand command) {
        String pathName = command.getPathName();

        directoryService.throwExceptionIfRootPath(pathName);

        //검색 디렉토리 존재 확인, 가져오기. 존재 X -> 실패 응답
        Directory directory = directoryService.checkExistAndGetDirectory(pathName);
        log.info("디렉토리 조회. pathName: {}", pathName);

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


    /**
     * 조회한 디렉토리의 created, modified 정보 포맷 변경
     * @return 변경된 created, modified 정보
     */
    private String localDateTimeToStr(LocalDateTime localDateTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return localDateTime.format(dateTimeFormatter);
    }

}
