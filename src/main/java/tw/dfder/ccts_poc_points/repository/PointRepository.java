package tw.dfder.ccts_poc_points.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tw.dfder.ccts_poc_points.Entity.UpdatePointsEnvelope;

@Repository
public interface PointRepository extends MongoRepository<UpdatePointsEnvelope,String> {
    UpdatePointsEnvelope findByPaymentId(String pid);
    UpdatePointsEnvelope findByBuyerId(String bid);

}
