package com.jwtauth.jwtauth.utils;


import com.jwtauth.jwtauth.model.UserEntity;
import com.jwtauth.jwtauth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NicUtil {
    public static final String NIC_PATTERN_REGEX = "^([0-9]{9}[x|X|v|V]|[0-9]{12})$";

    private final UserRepository userRepository;

    public boolean isValidNic(String nic) {
        return nic.matches(NIC_PATTERN_REGEX);
    }

    public boolean isUniqueNic(String nic) {
        List<String> nics = getFormattedNICList(nic);
        log.info("isUniqueNic-> formatted NICs: {}", nics);
        List<UserEntity> customers = userRepository.findAllByNicIn(nics);
        if (customers.isEmpty()) {
            log.info("isUniqueNic-> NICs are not on the db");
            return true;
        }
        log.warn("isUniqueNic-> customers found for given NICs");
        return false;
    }

    public List<String> getFormattedNICList(String userNic) {
        log.info("getFormattedNICList-> Get Formatted NIC List by --> {}", userNic);
        List<String> userNicList = new ArrayList<>();
        userNicList.add(userNic.toUpperCase());
        if (userNic.length() == 12) {
            StringBuilder sb = new StringBuilder(userNic.substring(2));
            sb.deleteCharAt(5);
            sb.append("V");
            userNicList.add(sb.toString());
            sb.setCharAt(9, 'X');
            userNicList.add(sb.toString());
            sb.setCharAt(9, 'v');
            userNicList.add(sb.toString());
            sb.setCharAt(9, 'x');
            userNicList.add(sb.toString());


        } else if (userNic.length() == 10) {
            //Old NIC to New NIC, nic --- > 933390268V

            StringBuilder sb = new StringBuilder(userNic.substring(0, userNic.length() - 1)); // result -> 933390268

            //add 02 more records with simple letter -->
            sb.append("v"); // result -> 933390268V
            userNicList.add(sb.toString());
            sb.setCharAt(9, 'X'); // result -> 933390268X
            userNicList.add(sb.toString());
            sb.setCharAt(9, 'x'); // result -> 933390268x
            userNicList.add(sb.toString());

            //remove last char -->
            sb.deleteCharAt(9); // result -> 933390268

            //conversion to new type -->
            int firstTwoNum = Integer.parseInt(sb.substring(0, 2));
            //Check first two numbers are less than 20
            if (firstTwoNum < 20) {
                sb.insert(0, "20");
            } else {
                sb.insert(0, "19");
            }
            sb.insert(7, "0"); // result -> 1993339[0]0268
            userNicList.add(sb.toString());
        }
        log.info("Formatted NIC list --> {}", userNicList);

        return userNicList;
    }
}

