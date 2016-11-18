package com.gabriel.domain.specification;

import com.gabriel.domain.Job;
import com.gabriel.domain.Job_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by liuyufei on 18/11/16.
 */
public class JobSpecification  {

//    public static Specification<Job> jobsInSpecificLocation(String location) {
//        return (root, query, cb) -> cb.equal(root.get(Job_.location), location);
//    }



    public static Specification<Job> findByCriteria(final Job jobCriteria) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (jobCriteria.getLocation() != null && !jobCriteria.getLocation().equals("All")) {
                predicates.add(cb.equal(root.get(Job_.location), jobCriteria.getLocation()));
            }

            if (jobCriteria.getSearchWord() != null) {
                predicates.add(cb.equal(root.get(Job_.searchWord), jobCriteria.getSearchWord()));
            }




            return cb.and(predicates.toArray(new Predicate[] {}));
        };
    }
}
