package dev.hyudoro.pay_my_buddy_service.service.impl;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import dev.hyudoro.pay_my_buddy_service.dto.ConnectionRequest;
import dev.hyudoro.pay_my_buddy_service.dto.UserConnectionResponse;
import dev.hyudoro.pay_my_buddy_service.entity.Connection;
import dev.hyudoro.pay_my_buddy_service.entity.ConnectionId;
import dev.hyudoro.pay_my_buddy_service.entity.User;
import dev.hyudoro.pay_my_buddy_service.exception.ConnectionAlreadyExistsException;
import dev.hyudoro.pay_my_buddy_service.exception.SelfConnectionException;
import dev.hyudoro.pay_my_buddy_service.exception.UserNotFoundException;
import dev.hyudoro.pay_my_buddy_service.repository.ConnectionRepository;
import dev.hyudoro.pay_my_buddy_service.repository.UserRepository;
import dev.hyudoro.pay_my_buddy_service.service.inter.ConnectionService;

@Service
public class ConnectionServiceImpl implements ConnectionService{
    private static final Logger log = LoggerFactory.getLogger(ConnectionServiceImpl.class);

    private final ConnectionRepository connectionRepository;
    private final UserRepository userRepository;

    ConnectionServiceImpl(ConnectionRepository connectionRepository, UserRepository userRepository){
        this.connectionRepository = connectionRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void addConnection(ConnectionRequest request) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        String connectionEmail = request.email();
        log.debug("Add connection request: {} -> {}", userEmail, connectionEmail);

        if(connectionEmail.equals(userEmail)){
            log.warn("Self-connection attempt by: {}", userEmail);
            throw new SelfConnectionException("user cannot add himself");
        }

        User user  = (userRepository.findByEmail(userEmail)).orElseThrow(() -> new UserNotFoundException("user not found"));
        //connection user does not exist if exception
        User connectionUser = (userRepository.findByEmail(connectionEmail)).orElseThrow(() -> new UserNotFoundException("connection user not found"));

        UUID smallestId = (user.getId().toString().compareTo(connectionUser.getId().toString())) < 0 ? user.getId() : connectionUser.getId();
        UUID largestId = (smallestId.equals(user.getId())) ? connectionUser.getId() : user.getId();

        //already a connection if exception
        if(connectionRepository.existsById(new ConnectionId(smallestId,largestId))) {
            log.warn("Connection already exists between {} and {}", userEmail, connectionEmail);
            throw new ConnectionAlreadyExistsException("user already connected");
        } else {
            connectionRepository.save(new Connection(smallestId.equals(user.getId()) ? user : connectionUser,
                                                     (largestId.equals(connectionUser.getId())) ? connectionUser : user));
            log.info("Connection established between {} and {}", userEmail, connectionEmail);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserConnectionResponse> listConnection() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        log.debug("Listing connections for: {}", userEmail);
        User currentUser = (userRepository.findByEmail(userEmail))
                         .orElseThrow(() -> new UserNotFoundException("user not found"));

        List<UserConnectionResponse> connections = connectionRepository
            .findConnectionsOf(currentUser.getId())
            .stream()
            .map(user -> new UserConnectionResponse(user.getUsername(),user.getId()))
            .toList();
        log.debug("Found {} connection(s) for: {}", connections.size(), userEmail);
        return connections;
    }
}
