package school.hei.stdgrade.service;

import java.util.List;
import java.util.NoSuchElementException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.stdgrade.model.AcademicTrack;
import school.hei.stdgrade.repository.JAcademicTrackRepository;
import school.hei.stdgrade.repository.mapper.JAcademicTrackMapper;
import school.hei.stdgrade.repository.model.JAcademicTrack;
import school.hei.stdgrade.service.validator.CrupdateAcademicTrackValidator;

@Service
@AllArgsConstructor
public class AcademicTrackService {

  private final JAcademicTrackRepository jRepository;
  private final JAcademicTrackMapper jMapper;
  private final CrupdateAcademicTrackValidator crupdateValidator;

  public List<AcademicTrack> findAll() {
    return jRepository.findAll().stream().map(jMapper::toDomain).toList();
  }

  public AcademicTrack getById(String id) {
    return jRepository
        .findById(id)
        .map(jMapper::toDomain)
        .orElseThrow(() -> new NoSuchElementException("AcademicTrack(id=" + id + ") not found"));
  }

  public AcademicTrack save(AcademicTrack toSave) {
    var existingByCode = jRepository.findByCode(toSave.code());

    String targetId = existingByCode.map(JAcademicTrack::getId).orElse(toSave.id());

    AcademicTrack trackToValidate = toSave.toBuilder().id(targetId).build();

    crupdateValidator.accept(trackToValidate);

    JAcademicTrack entityToSave =
        jRepository.findById(targetId).orElseGet(() -> jMapper.toEntity(trackToValidate));

    entityToSave.setCode(trackToValidate.code());
    entityToSave.setName(trackToValidate.name());

    return jMapper.toDomain(jRepository.save(entityToSave));
  }
}
