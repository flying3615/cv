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
public class JobSpecification {

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

            return cb.and(predicates.toArray(new Predicate[]{}));
        };
    }


    public static void main(String[] args) {




//        Job job = new Job();
//
//        Class jobClazz = Job.class;
//        Class job_clazz = Job_.class;
//
//        Method[] methods = jobClazz.getMethods();
//        Arrays.asList(methods).stream().filter(m -> m.getName().startsWith("get") && !m.getName().equals("getClass")).forEach(m -> {
//            try {
//                //get fields' names
//                String methodName = m.getName();
//
//                String fieldWithUpperCaseFirstLetter = methodName.substring("get".length(), methodName.length());
//
//                String fieldName = fieldWithUpperCaseFirstLetter.substring(0, 1).toLowerCase() + fieldWithUpperCaseFirstLetter.substring(1);
//
//                System.out.println(fieldName);
//                System.out.println(job_clazz.getField(fieldName));
//
//
//                System.out.println(); //get Job_ fields' names
//                if(m.invoke(jobCriteria, null)!=null){
//                    predicates.add(cb.equal(root.get(job_clazz.getField(fieldName).get(job_clazz)), m.invoke(jobCriteria, null)));
//
//                } //invoke getXXX
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });



    }
}
