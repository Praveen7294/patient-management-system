package org.example.patientservice.service;

import org.example.patientservice.exception.EmailAlreadyExistException;
import org.example.patientservice.exception.PatientNotFoundException;
import org.example.patientservice.dto.PatientRequestDTO;
import org.example.patientservice.dto.PatientResponseDTO;
import org.example.patientservice.grpc.BillingServiceGrpcClient;
import org.example.patientservice.mapper.PatientMapper;
import org.example.patientservice.model.Patient;
import org.example.patientservice.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {

    public final PatientRepository patientRepository;
    public final BillingServiceGrpcClient billingServiceGrpcClient;

    public PatientService(PatientRepository patientRepository,
                          BillingServiceGrpcClient billingServiceGrpcClient) {
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
    }

    public List<PatientResponseDTO> getPatients() {
        List<Patient> patients = patientRepository.findAll();

        return patients.stream().map(PatientMapper::toDTO).toList();
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {

        if (patientRepository.existsByEmail(patientRequestDTO.getEmail())) {
            throw new EmailAlreadyExistException("A patient with this email already exist "
                    + patientRequestDTO.getEmail());
        }

        Patient newPatient = patientRepository.save(PatientMapper.toModel(patientRequestDTO));

        billingServiceGrpcClient.createBillingAccount(
                newPatient.getId().toString(), newPatient.getName(), newPatient.getEmail());

        return PatientMapper.toDTO(newPatient);
    }

    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO) {

        Patient patient = patientRepository.findById(id).orElseThrow(
                () -> new PatientNotFoundException("Patient not found with the ID: " + id));

        if (patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(), id)) {
            throw new EmailAlreadyExistException("A patient with this email already exist "
                    + patientRequestDTO.getEmail());
        }

        patient.setName(patientRequestDTO.getName());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));

        Patient updatedPatient = patientRepository.save(patient);

        return PatientMapper.toDTO(updatedPatient);
    }

    public void deletePatient(UUID id) {

        Patient patient = patientRepository.findById(id).orElseThrow(
                () -> new PatientNotFoundException("Patient not found with the ID: " + id));

        patientRepository.delete(patient);
    }
}
