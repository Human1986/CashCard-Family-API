package example.cashcard.controllers;

import example.cashcard.CashCard;
import example.cashcard.repositories.CashCardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;

@RestController
//This tells Spring that this class is a Component of type RestController and capable of handling HTTP requests
@RequestMapping("/cashcards")  //This is a companion to @RestController that indicates which address requests must have to access this Controller.
class CashCardController {

    private final CashCardRepository cashCardRepository;

    private CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    /*
      This is data management.
      Our Controller shouldn't be concerned with checking IDs or creating data.
     */

    /**
     * @GetMapping marks a method as a handler method.
     * GET requests that match cashcards/{requestedID} will be handled by this method.
     */

    @GetMapping("/requestedId")
    private ResponseEntity<CashCard> findById(@PathVariable Long requestedId) {
        Optional<CashCard> cashCardOptional = cashCardRepository.findById(requestedId);
        if (cashCardOptional.isPresent()) {
            return ResponseEntity.ok(cashCardOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     The POST endpoint is similar to the GET endpoint in our CashCardController,
     but uses the @PostMapping annotation from Spring Web.
     */
    @PostMapping
    private ResponseEntity<Void> createCashCard(@RequestBody CashCard newCashCardRequest, UriComponentsBuilder ucb) {
        CashCard savedCashCard = cashCardRepository.save(newCashCardRequest);
        URI locationOfNewCashCard = ucb
                .path("cashcards/{id}")
                .buildAndExpand(savedCashCard.id())
                .toUri();
        return ResponseEntity.created(locationOfNewCashCard).build();
    }


    /**
     * Once again we're using one of Spring Data's built-in implementations:
     * CrudRepository.findAll(). Our implementing Repository, CashCardRepository,
     * will automatically return all CashCard records from the database when findAll() is invoked.
     */


    @GetMapping
    private ResponseEntity<List<CashCard>> findAll(Pageable pageable) {
        Page<CashCard> page = cashCardRepository.findAll(
                PageRequest.of(              //PageRequest is a basic Java Bean implementation of Pageable
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))
                ));
        return ResponseEntity.ok(page.getContent());
    }

}

