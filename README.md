
import { useState, type FormEvent } from "react";
import { useNavigate } from "react-router-dom";

import {
  Alert,
  Box,
  Button,
  IconButton,
  InputAdornment,
  Paper,
  TextField,
  Typography,
} from "@mui/material";

import Visibility from "@mui/icons-material/Visibility";
import VisibilityOff from "@mui/icons-material/VisibilityOff";

import { useAuth } from "./AuthContext";

export default function LoginPage() {
  const [form, setForm] = useState({
    email: "",
    password: "",
  });

  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    setError("");
    setLoading(true);

    try {
      await login(form);
      navigate("/", { replace: true });
    } catch {
      setError("Neispravan email ili lozinka.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box
      component="main"
      sx={{
        minHeight: "100vh",
        width: "100%",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        px: 3,
        py: 8,
        backgroundColor: "rgba(37, 37, 37, 0.3)",
        backdropFilter: "blur(8px)",

        // Ako imaš sliku u public folderu:
        // backgroundImage:
        //   "linear-gradient(rgba(37,37,37,0.3), rgba(37,37,37,0.3)), url('/parking-bg.jpg')",
        // backgroundSize: "cover",
        // backgroundPosition: "center",
      }}
    >
      <Paper
        component="form"
        onSubmit={handleSubmit}
        elevation={0}
        sx={{
          width: "600px",
          height: "529px",
          borderRadius: "16px",
          border: "2px solid #ffffff",
          backgroundColor: "rgba(37, 37, 37, 0.85)",
          color: "#ffffff",
          px: "32px",
          pt: "16px",
          pb: "32px",
          display: "flex",
          flexDirection: "column",
          gap: "80px",
          boxShadow: "0 4px 4px rgba(0, 0, 0, 0.25)",
        }}
      >
        <Box
          sx={{
            width: "100%",
            height: "40px",
            display: "flex",
            alignItems: "center",
          }}
        >
          <Typography
            variant="h4"
            component="h1"
            sx={{
              fontWeight: 700,
              color: "#ffffff",
            }}
          >
            Login
          </Typography>
        </Box>

        <Box
          sx={{
            width: "100%",
            height: "164px",
            display: "flex",
            flexDirection: "column",
            gap: "16px",
          }}
        >
          <TextField
            label="E-mail *"
            type="email"
            value={form.email}
            onChange={(e) => setForm({ ...form, email: e.target.value })}
            fullWidth
            variant="outlined"
            InputLabelProps={{
              shrink: true,
            }}
            sx={textFieldSx}
          />

          <TextField
            label="Password *"
            type={showPassword ? "text" : "password"}
            value={form.password}
            onChange={(e) => setForm({ ...form, password: e.target.value })}
            fullWidth
            variant="outlined"
            InputLabelProps={{
              shrink: true,
            }}
            sx={textFieldSx}
            InputProps={{
              endAdornment: (
                <InputAdornment position="end">
                  <IconButton
                    type="button"
                    onClick={() => setShowPassword((prev) => !prev)}
                    edge="end"
                    sx={{
                      color: "#ffffff",
                    }}
                  >
                    {showPassword ? <VisibilityOff /> : <Visibility />}
                  </IconButton>
                </InputAdornment>
              ),
            }}
          />
        </Box>

        <Box
          sx={{
            width: "288px",
            height: "35px",
            alignSelf: "center",
            display: "flex",
            flexDirection: "column",
            gap: "8px",
          }}
        >
          {error && (
            <Alert
              severity="error"
              sx={{
                mb: 1,
                py: 0,
                fontSize: "13px",
              }}
            >
              {error}
            </Alert>
          )}

          <Button
            type="submit"
            disabled={loading}
            variant="contained"
            disableElevation
            sx={{
              width: "288px",
              height: "35px",
              borderRadius: "24px",
              backgroundColor: "#7EFFC6",
              color: "#252525",
              textTransform: "none",
              fontSize: "16px",
              fontWeight: 500,

              "&:hover": {
                backgroundColor: "#6be3b1",
              },

              "&:disabled": {
                backgroundColor: "#7EFFC6",
                opacity: 0.7,
                color: "#252525",
              },
            }}
          >
            {loading ? "Loading..." : "Login"}
          </Button>
        </Box>
      </Paper>
    </Box>
  );
}

const textFieldSx = {
  "& .MuiInputLabel-root": {
    color: "#ffffff",
    fontWeight: 700,
    fontSize: "16px",
  },

  "& .MuiInputLabel-root.Mui-focused": {
    color: "#ffffff",
  },

  "& .MuiOutlinedInput-root": {
    height: "48px",
    color: "#ffffff",
    borderRadius: "8px",

    "& fieldset": {
      borderColor: "#ffffff",
      borderWidth: "2px",
    },

    "&:hover fieldset": {
      borderColor: "#ffffff",
    },

    "&.Mui-focused fieldset": {
      borderColor: "#7EFFC6",
    },
  },

  "& input": {
    color: "#ffffff",
  },
};
