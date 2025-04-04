package g06.ecnu.heartbridge.service;

import g06.ecnu.heartbridge.DTO.ConsultantDetailDTO;
import g06.ecnu.heartbridge.DTO.SuggestConsultantListDTO;
import g06.ecnu.heartbridge.DTO.UserWithPreferAndArticleHistoryDTO;
import g06.ecnu.heartbridge.mapper.ConsultantMapper;
import g06.ecnu.heartbridge.mapper.ConsultantSearchMapper;
import g06.ecnu.heartbridge.mapper.UserArticleHistoryMapper;
import g06.ecnu.heartbridge.pojo.ConsultantDetail;
import g06.ecnu.heartbridge.utils.ConsultantSuggest;
import g06.ecnu.heartbridge.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author 璃樘鼎臻
 * @since 2025/4/3 下午10:50
 **/
@Service
public class ConsultantSuggestService {
    @Autowired
    UserArticleHistoryMapper userArticleHistoryMapper;
    @Autowired
    BeanFactory beanFactory;
    @Autowired
    ConsultantMapper consultantMapper;
    @Autowired
    private ConsultantSearchMapper consultantSearchMapper;

    private ConsultantSuggest initConsultantSuggest(UserWithPreferAndArticleHistoryDTO dto){
        ConsultantSuggest suggest= beanFactory.getBean(ConsultantSuggest.class);
        suggest.setTags(new HashSet<>(dto.getPreferTags()));
        TreeMap<String,Integer> map=new TreeMap<>();
        for(int i=0;i<dto.getHistoryTags().size();i++){
            String str=dto.getHistoryTags().get(i);
            if(map.containsKey(str)){
                map.put(str,map.get(str)+1);
            }else {
                map.put(str,1);
            }
        }
        suggest.setHistories(map);
        return suggest;
    }
    public ResponseEntity<SuggestConsultantListDTO> getSuggestConsultants(Integer count,HttpServletRequest request){
        String jwt=request.getHeader("Authorization").substring(7);
        int userId= JwtUtil.validateToken(jwt).get("userId", Integer.class);
        UserWithPreferAndArticleHistoryDTO dto=userArticleHistoryMapper.getRecord(userId);
        ConsultantSuggest suggest= initConsultantSuggest(dto);
        List<ConsultantDetail> list=consultantSearchMapper.getAllConsultants();
        HashMap<Integer, ConsultantDetailDTO>map=new HashMap<>();
        for (ConsultantDetail detail : list) {
            if (map.containsKey(detail.getId())) {
                map.get(detail.getId()).getTags().add(detail.getTag());
            } else {
                ConsultantDetailDTO detailDTO = new ConsultantDetailDTO();
                detailDTO.setId(detail.getId());
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add(detail.getTag());
                detailDTO.setTags(arrayList);
                detailDTO.setProfile("1");
                detailDTO.setAvgScore(detail.getAvgScore());
                detailDTO.setUsername(detail.getUsername());
                map.put(detail.getId(), detailDTO);
            }
        }
        ArrayList<ConsultantDetailDTO> consultantDetailDTOS = new ArrayList<>(map.values());
        HashMap<Integer,Double>scoreMap=new HashMap<>();
        for (ConsultantDetailDTO consultantDetailDTO : consultantDetailDTOS) {
            scoreMap.put(consultantDetailDTO.getId(), suggest.getSuggestParam(consultantDetailDTO.getTags(), consultantDetailDTO.getAvgScore()));
        }
        consultantDetailDTOS.sort((a,b)->{
            return Double.compare(scoreMap.get(b.getId()),scoreMap.get(a.getId()));
        });
        ArrayList<ConsultantDetailDTO>result=new ArrayList<>();
        for (ConsultantDetailDTO consultantDetailDTO : consultantDetailDTOS) {
            if (consultantDetailDTO.getId() == userId) continue;
            consultantDetailDTO.setProfile(consultantMapper.getConsultantById(consultantDetailDTO.getId()).getProfile());
            result.add(consultantDetailDTO);
            if (result.size() == count) break;
        }
        SuggestConsultantListDTO suggestConsultantListDTO=new SuggestConsultantListDTO();
        suggestConsultantListDTO.setData(result);
        return ResponseEntity.ok(suggestConsultantListDTO);
    }
}
