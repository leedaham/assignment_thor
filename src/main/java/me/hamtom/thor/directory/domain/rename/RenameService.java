package me.hamtom.thor.directory.domain.rename;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.hamtom.thor.directory.domain.common.DirectoryService;
import me.hamtom.thor.directory.domain.rename.dto.RenameDirectoryDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RenameService {
    private final DirectoryService directoryService;

    public void renameDirectory(RenameDirectoryDto renameDirectoryDto) {
    }
}
