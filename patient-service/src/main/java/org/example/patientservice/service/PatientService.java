package org.example.patientservice.service;

import org.example.patientservice.repository.PatientRepository;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

    public PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }
}
