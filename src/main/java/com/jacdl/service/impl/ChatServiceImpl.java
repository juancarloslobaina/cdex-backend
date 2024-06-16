package com.jacdl.service.impl;

import com.jacdl.domain.Chat;
import com.jacdl.repository.ChatRepository;
import com.jacdl.service.ChatService;
import com.jacdl.service.dto.ChatDTO;
import com.jacdl.service.mapper.ChatMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.jacdl.domain.Chat}.
 */
@Service
@Transactional
public class ChatServiceImpl implements ChatService {

    private final Logger log = LoggerFactory.getLogger(ChatServiceImpl.class);

    private final ChatRepository chatRepository;

    private final ChatMapper chatMapper;

    public ChatServiceImpl(ChatRepository chatRepository, ChatMapper chatMapper) {
        this.chatRepository = chatRepository;
        this.chatMapper = chatMapper;
    }

    @Override
    public ChatDTO save(ChatDTO chatDTO) {
        log.debug("Request to save Chat : {}", chatDTO);
        Chat chat = chatMapper.toEntity(chatDTO);
        chat = chatRepository.save(chat);
        return chatMapper.toDto(chat);
    }

    @Override
    public ChatDTO update(ChatDTO chatDTO) {
        log.debug("Request to update Chat : {}", chatDTO);
        Chat chat = chatMapper.toEntity(chatDTO);
        chat = chatRepository.save(chat);
        return chatMapper.toDto(chat);
    }

    @Override
    public Optional<ChatDTO> partialUpdate(ChatDTO chatDTO) {
        log.debug("Request to partially update Chat : {}", chatDTO);

        return chatRepository
            .findById(chatDTO.getId())
            .map(existingChat -> {
                chatMapper.partialUpdate(existingChat, chatDTO);

                return existingChat;
            })
            .map(chatRepository::save)
            .map(chatMapper::toDto);
    }

    public Page<ChatDTO> findAllWithEagerRelationships(Pageable pageable) {
        return chatRepository.findAllWithEagerRelationships(pageable).map(chatMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ChatDTO> findOne(Long id) {
        log.debug("Request to get Chat : {}", id);
        return chatRepository.findOneWithEagerRelationships(id).map(chatMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Chat : {}", id);
        chatRepository.deleteById(id);
    }
}
