import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { login as loginApi, getCurrentUser } from '@/api/auth';
import type { LoginForm, UserInfo } from '@/types';

interface AuthState {
  user: UserInfo | null;
  loading: boolean;
}

const initialState: AuthState = {
  user: null,
  loading: false,
};

export const login = createAsyncThunk('auth/login', async (form: LoginForm) => {
  const res = await loginApi(form);
  localStorage.setItem('token', res.data.token);
  return res.data;
});

export const fetchCurrentUser = createAsyncThunk('auth/fetchCurrentUser', async () => {
  const res = await getCurrentUser();
  return res.data;
});

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    logout(state) {
      state.user = null;
      state.loading = false;
      localStorage.removeItem('token');
    },
    setUser(state, action: PayloadAction<UserInfo>) {
      state.user = action.payload;
      state.loading = false;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(login.pending, (state) => { state.loading = true; })
      .addCase(login.fulfilled, (state, action) => {
        state.loading = false;
        state.user = action.payload;
      })
      .addCase(login.rejected, (state) => {
        state.loading = false;
      })
      .addCase(fetchCurrentUser.pending, (state) => {
        state.loading = true;
      })
      .addCase(fetchCurrentUser.fulfilled, (state, action) => {
        state.loading = false;
        state.user = action.payload;
      })
      .addCase(fetchCurrentUser.rejected, (state) => {
        state.loading = false;
        state.user = null;
        localStorage.removeItem('token');
      });
  },
});

export const { logout, setUser } = authSlice.actions;
export default authSlice.reducer;
