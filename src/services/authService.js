import {
    createUserWithEmailAndPassword,
    signInWithEmailAndPassword,
    signInWithPopup,
    GoogleAuthProvider,
    signOut
} from "firebase/auth";

import { auth } from "../config/firebase";

const googleProvider = new GoogleAuthProvider();

export async function registerWithEmail(email, password) {
    const result = await createUserWithEmailAndPassword(
        auth,
        email,
        password
    );

    return result.user;
}

export async function loginWithEmail(email, password) {
    const result = await signInWithEmailAndPassword(
        auth,
        email,
        password
    );

    return result.user;
}

export async function loginWithGoogle() {
    const result = await signInWithPopup(
        auth,
        googleProvider
    );

    return result.user;
}

export async function logoutUser() {
    await signOut(auth);
}